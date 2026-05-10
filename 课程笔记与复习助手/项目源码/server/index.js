const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const fs = require('fs');
const path = require('path');

const app = express();
const PORT = 3000;

// 中间件
app.use(cors());
app.use(bodyParser.json());

// =======================
// 简易 Admin 监控面板支持
// =======================
// 访问地址：http://localhost:3000/admin
// 可选鉴权：设置环境变量 ADMIN_TOKEN 后，需要在请求中带 token
// - Header: x-admin-token: <token>
// - 或 Query: ?token=<token>
const ADMIN_TOKEN = process.env.ADMIN_TOKEN || '';
function requireAdminToken(req, res, next) {
  if (!ADMIN_TOKEN) return next(); // 未设置则不启用鉴权
  const token = (req.query && req.query.token) || req.get('x-admin-token') || '';
  if (token && token === ADMIN_TOKEN) return next();
  return res.status(401).json({ success: false, message: 'Unauthorized: admin token required' });
}

// 最近请求日志（简版）：环形缓冲，避免无限增长
const recentRequests = [];
const MAX_REQUEST_LOGS = 200;
function pushRequestLog(entry) {
  recentRequests.push(entry);
  if (recentRequests.length > MAX_REQUEST_LOGS) {
    recentRequests.splice(0, recentRequests.length - MAX_REQUEST_LOGS);
  }
}

// 记录请求（需在路由之前注册）
app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    const p = req.path || '';
    // 过滤静态资源噪声（可按需调整）
    if (p.startsWith('/admin/assets/')) return;
    pushRequestLog({
      time: Date.now(),
      method: req.method,
      path: req.originalUrl || req.url,
      status: res.statusCode,
      durationMs: Date.now() - start,
      ip: req.headers['x-forwarded-for'] || req.socket.remoteAddress || ''
    });
  });
  next();
});

// 备份文件目录
const backupDir = path.join(__dirname, 'backups');
if (!fs.existsSync(backupDir)) {
  fs.mkdirSync(backupDir, { recursive: true });
}

// 课表分享目录
const timetableDir = path.join(__dirname, 'timetables');
if (!fs.existsSync(timetableDir)) {
  fs.mkdirSync(timetableDir, { recursive: true });
}

// 自定义计划分享目录
const customScheduleDir = path.join(__dirname, 'custom_schedules');
if (!fs.existsSync(customScheduleDir)) {
  fs.mkdirSync(customScheduleDir, { recursive: true });
}

// 班级发布计划目录（存放每个班级的发布计划列表）
const classPlansDir = path.join(__dirname, 'class_plans');
if (!fs.existsSync(classPlansDir)) {
  fs.mkdirSync(classPlansDir, { recursive: true });
}

// Admin 静态页面
app.use('/admin/assets', express.static(path.join(__dirname, 'public', 'admin')));
app.get('/admin', (req, res) => {
  res.sendFile(path.join(__dirname, 'public', 'admin', 'index.html'));
});

// 工具函数：根据 userId 找到备份文件路径
function backupFilePath(userId) {
  return path.join(backupDir, `${userId}.json`);
}

// 工具函数：根据 shareId 找到课表分享文件路径
function timetableFilePath(shareId) {
  return path.join(timetableDir, `${shareId}.json`);
}

// 工具函数：根据 shareId 找到自定义计划分享文件路径
function customScheduleFilePath(shareId) {
  return path.join(customScheduleDir, `${shareId}.json`);
}

// 工具函数：根据 className 找到班级发布计划文件路径
function classKeyFromName(className) {
  // encodeURIComponent 可处理中文；把 % 换成 _ 让文件名更直观
  return encodeURIComponent(String(className || '')).replace(/%/g, '_');
}
function classPlansFilePath(className) {
  return path.join(classPlansDir, `${classKeyFromName(className)}.json`);
}

/**
 * POST /api/backup
 * 请求体示例：
 * {
 *   "userId": 1,
 *   "timestamp": 1732780000000,
 *   "payload": { ... 需要备份的全部数据 ... }
 * }
 */
app.post('/api/backup', (req, res) => {
  const { userId, timestamp, payload } = req.body || {};

  // 调试日志：打印收到的完整请求体，便于排查字段是否正确
  console.log('POST /api/backup body =', JSON.stringify(req.body, null, 2));

  if (userId == null || payload == null) {
    console.warn('Invalid backup request, userId or payload missing.');
    return res.status(400).json({
      success: false,
      message: 'userId 与 payload 为必填字段'
    });
  }

  const file = backupFilePath(userId);
  const dataToSave = {
    userId,
    timestamp: timestamp || Date.now(),
    payload
  };

  fs.writeFile(file, JSON.stringify(dataToSave, null, 2), 'utf8', err => {
    if (err) {
      console.error('写入备份失败:', err);
      return res.status(500).json({
        success: false,
        message: '服务器写入备份失败'
      });
    }
    console.log(`Backup saved for user ${userId}`);
    res.json({
      success: true,
      message: '备份成功'
    });
  });
});

/**
 * POST /api/timetable/share
 * 用于生成“公开课表分享链接”。
 * 请求体示例：
 * {
 *   "timetable": {
 *     "planTitle": "2025-2026学年第1学期 第2周",
 *     "cells": [
 *       { "rowIndex": 0, "colIndex": 2, "title": "操作系统", "location": "...", "content": "..." },
 *       ...
 *     ]
 *   }
 * }
 * 响应示例：
 * {
 *   "success": true,
 *   "shareId": "abc123",
 *   "shareUrl": "http://192.168.43.201:3000/api/timetable/abc123"
 * }
 */
app.post('/api/timetable/share', (req, res) => {
  const timetable = (req.body && req.body.timetable) || null;

  if (!timetable || !Array.isArray(timetable.cells) || timetable.cells.length === 0) {
    return res.status(400).json({
      success: false,
      message: '课表数据为空，无法生成分享链接'
    });
  }

  // 简单生成一个 shareId：时间戳 + 随机串
  const shareId =
    Date.now().toString(36) + Math.random().toString(36).substring(2, 8);

  const file = timetableFilePath(shareId);
  const dataToSave = {
    shareId,
    createdAt: Date.now(),
    timetable
  };

  fs.writeFile(file, JSON.stringify(dataToSave, null, 2), 'utf8', err => {
    if (err) {
      console.error('写入课表分享文件失败:', err);
      return res.status(500).json({
        success: false,
        message: '服务器保存课表分享失败'
      });
    }

    const host = req.get('host') || `localhost:${PORT}`;
    const protocol = req.protocol || 'http';
    const shareUrl = `${protocol}://${host}/api/timetable/${shareId}`;

    console.log(`Timetable shared: ${shareId}`);
    res.json({
      success: true,
      shareId,
      shareUrl
    });
  });
});

/**
 * GET /api/timetable/:shareId
 * 根据分享 ID 返回此前保存的课表 JSON。
 */
app.get('/api/timetable/:shareId', (req, res) => {
  const shareId = req.params.shareId;
  if (!shareId) {
    return res.status(400).json({
      success: false,
      message: '缺少 shareId 参数'
    });
  }

  const file = timetableFilePath(shareId);
  if (!fs.existsSync(file)) {
    return res.status(404).json({
      success: false,
      message: '分享链接不存在或已失效'
    });
  }

  fs.readFile(file, 'utf8', (err, content) => {
    if (err) {
      console.error('读取课表分享文件失败:', err);
      return res.status(500).json({
        success: false,
        message: '服务器读取课表分享失败'
      });
    }

    try {
      const json = JSON.parse(content);
      res.json({
        success: true,
        shareId,
        timetable: json.timetable
      });
    } catch (e) {
      console.error('解析课表分享 JSON 失败:', e);
      res.status(500).json({
        success: false,
        message: '课表分享文件已损坏'
      });
    }
  });
});

/**
 * POST /api/customSchedule/share
 * 用于生成“自定义计划分享链接”。
 * 请求体示例：
 * {
 *   "customSchedule": {
 *     "planTitle": "某自定义计划",
 *     "startDayEpoch": 12345,
 *     "endDayEpoch": 12349,
 *     "events": [
 *       { "dateEpochDay": 12345, "startMinutes": 480, "endMinutes": 540, "title": "...", "content": "..." },
 *       ...
 *     ]
 *   }
 * }
 */
app.post('/api/customSchedule/share', (req, res) => {
  const customSchedule = (req.body && req.body.customSchedule) || null;

  if (
    !customSchedule ||
    !Array.isArray(customSchedule.events) ||
    customSchedule.events.length === 0
  ) {
    return res.status(400).json({
      success: false,
      message: '自定义计划数据为空，无法生成分享链接'
    });
  }

  const shareId =
    Date.now().toString(36) + Math.random().toString(36).substring(2, 8);

  const file = customScheduleFilePath(shareId);
  const dataToSave = {
    shareId,
    createdAt: Date.now(),
    customSchedule
  };

  fs.writeFile(file, JSON.stringify(dataToSave, null, 2), 'utf8', err => {
    if (err) {
      console.error('写入自定义计划分享文件失败:', err);
      return res.status(500).json({
        success: false,
        message: '服务器保存自定义计划分享失败'
      });
    }

    const host = req.get('host') || `localhost:${PORT}`;
    const protocol = req.protocol || 'http';
    const shareUrl = `${protocol}://${host}/api/customSchedule/${shareId}`;

    console.log(`Custom schedule shared: ${shareId}`);
    res.json({
      success: true,
      shareId,
      shareUrl
    });
  });
});

/**
 * GET /api/customSchedule/:shareId
 * 根据分享 ID 返回此前保存的自定义计划 JSON。
 */
app.get('/api/customSchedule/:shareId', (req, res) => {
  const shareId = req.params.shareId;
  if (!shareId) {
    return res.status(400).json({
      success: false,
      message: '缺少 shareId 参数'
    });
  }

  const file = customScheduleFilePath(shareId);
  if (!fs.existsSync(file)) {
    return res.status(404).json({
      success: false,
      message: '分享链接不存在或已失效'
    });
  }

  fs.readFile(file, 'utf8', (err, content) => {
    if (err) {
      console.error('读取自定义计划分享文件失败:', err);
      return res.status(500).json({
        success: false,
        message: '服务器读取自定义计划分享失败'
      });
    }

    try {
      const json = JSON.parse(content);
      res.json({
        success: true,
        shareId,
        customSchedule: json.customSchedule
      });
    } catch (e) {
      console.error('解析自定义计划分享 JSON 失败:', e);
      res.status(500).json({
        success: false,
        message: '自定义计划分享文件已损坏'
      });
    }
  });
});

/**
 * GET /api/backup?userId=1
 */
app.get('/api/backup', (req, res) => {
  const userId = req.query.userId;
  if (userId == null) {
    return res.status(400).json({
      success: false,
      message: '缺少 userId 参数'
    });
  }

  const file = backupFilePath(userId);
  if (!fs.existsSync(file)) {
    return res.status(404).json({
      success: false,
      message: '该用户暂无云端备份'
    });
  }

  fs.readFile(file, 'utf8', (err, content) => {
    if (err) {
      console.error('读取备份失败:', err);
      return res.status(500).json({
        success: false,
        message: '服务器读取备份失败'
      });
    }

    try {
      const json = JSON.parse(content);
      res.json({
        success: true,
        ...json
      });
    } catch (e) {
      console.error('解析备份 JSON 失败:', e);
      res.status(500).json({
        success: false,
        message: '备份文件已损坏'
      });
    }
  });
});

// ======== 手机号验证码登录相关（教学用示例） ========

// 简单的内存存储结构：phone -> { code, expireAt }
const smsStore = {};
const CODE_EXPIRE_MS = 5 * 60 * 1000; // 5 分钟

// 最近一次生成的验证码（用于管理端展示）
let lastSmsCodeRecord = null; // { phoneMasked, code, expireAt, createdAt }

function generateCode() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

function cleanupExpiredSms() {
  const now = Date.now();
  Object.keys(smsStore).forEach(phone => {
    const rec = smsStore[phone];
    if (!rec || now > rec.expireAt) delete smsStore[phone];
  });
}

// =======================
// Admin API（可视化面板使用）
// =======================
function safeReaddir(dir) {
  try {
    if (!fs.existsSync(dir)) return [];
    return fs.readdirSync(dir);
  } catch (_) {
    return [];
  }
}

function formatBytes(bytes) {
  const b = Number(bytes || 0);
  if (b < 1024) return `${b} B`;
  const kb = b / 1024;
  if (kb < 1024) return `${kb.toFixed(2)} KB`;
  const mb = kb / 1024;
  if (mb < 1024) return `${mb.toFixed(2)} MB`;
  const gb = mb / 1024;
  return `${gb.toFixed(2)} GB`;
}

function sumDirBytes(dir, fileFilter) {
  let total = 0;
  const files = safeReaddir(dir);
  files.forEach(name => {
    if (fileFilter && !fileFilter(name)) return;
    const fp = path.join(dir, name);
    try {
      const st = fs.statSync(fp);
      if (st.isFile()) total += st.size;
    } catch (_) {}
  });
  return total;
}

function listBackupFiles() {
  const files = safeReaddir(backupDir)
    .filter(n => n.toLowerCase().endsWith('.json'))
    .map(name => {
      const fullPath = path.join(backupDir, name);
      let stat = null;
      try {
        stat = fs.statSync(fullPath);
      } catch (_) {}
      const userId = name.replace(/\.json$/i, '');
      const sizeBytes = stat && stat.isFile() ? stat.size : 0;
      const mtimeMs = stat && stat.isFile() ? stat.mtimeMs : 0;
      return {
        userId,
        fileName: name,
        sizeBytes,
        sizeText: formatBytes(sizeBytes),
        mtimeMs
      };
    })
    .sort((a, b) => b.mtimeMs - a.mtimeMs);
  return files;
}

// 健康检查（面板用）
app.get('/api/admin/health', requireAdminToken, (req, res) => {
  res.json({
    success: true,
    ok: true,
    now: Date.now(),
    uptimeSeconds: Math.floor(process.uptime()),
    port: PORT
  });
});

// 总览统计（面板用）
app.get('/api/admin/overview', requireAdminToken, (req, res) => {
  cleanupExpiredSms();
  const backups = listBackupFiles();
  const backupDirSizeBytes = sumDirBytes(backupDir, n => n.toLowerCase().endsWith('.json'));
  const smsActiveCount = Object.keys(smsStore).length;

  res.json({
    success: true,
    uptimeSeconds: Math.floor(process.uptime()),
    backup: {
      dir: backupDir,
      userBackupCount: backups.length, // backups/{userId}.json -> 数量≈有备份的用户数
      filesCount: backups.length,
      dirSizeBytes: backupDirSizeBytes,
      dirSizeText: formatBytes(backupDirSizeBytes),
      latestMtimeMs: backups[0] ? backups[0].mtimeMs : 0
    },
    sms: {
      activeCount: smsActiveCount
    }
  });
});

// 备份列表（面板用）
app.get('/api/admin/backups', requireAdminToken, (req, res) => {
  const backups = listBackupFiles();
  res.json({ success: true, backups });
});

// 最近请求日志（简版）
app.get('/api/admin/requests', requireAdminToken, (req, res) => {
  const limit = Math.max(1, Math.min(Number(req.query.limit || 50), 200));
  const items = recentRequests.slice(-limit).reverse();
  res.json({ success: true, items, totalKept: recentRequests.length });
});

// 短信验证码缓存状态（可选）
app.get('/api/admin/sms', requireAdminToken, (req, res) => {
  cleanupExpiredSms();
  const now = Date.now();
  const items = Object.keys(smsStore)
    .map(phone => {
      const rec = smsStore[phone];
      const masked = phone.replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2');
      return {
        phoneMasked: masked,
        expireAt: rec.expireAt,
        expiresInMs: Math.max(0, rec.expireAt - now)
      };
    })
    .sort((a, b) => a.expiresInMs - b.expiresInMs);
  res.json({ success: true, activeCount: items.length, items });
});

// 最新生成的验证码（管理端展示用）
app.get('/api/admin/sms/latest', requireAdminToken, (req, res) => {
  cleanupExpiredSms();
  if (!lastSmsCodeRecord) {
    return res.json({ success: true, latest: null });
  }
  // 若已过期则清空
  if (Date.now() > lastSmsCodeRecord.expireAt) {
    lastSmsCodeRecord = null;
    return res.json({ success: true, latest: null });
  }
  res.json({ success: true, latest: lastSmsCodeRecord });
});

// =======================
// 班级发布学习计划（简化版）
// =======================
// 约定：
// - 管理端发布：POST /api/classPlans/publish（可选需 ADMIN_TOKEN）
// - 学生端拉取：GET  /api/classPlans?className=xxx（无需 token）

function readJsonFileOrDefault(filePath, defaultValue) {
  try {
    if (!fs.existsSync(filePath)) return defaultValue;
    const raw = fs.readFileSync(filePath, 'utf8');
    return JSON.parse(raw);
  } catch (_) {
    return defaultValue;
  }
}

function writeJsonFile(filePath, obj) {
  fs.writeFileSync(filePath, JSON.stringify(obj, null, 2), 'utf8');
}

app.post('/api/classPlans/publish', requireAdminToken, (req, res) => {
  const { className, customSchedule } = req.body || {};
  const name = String(className || '').trim();
  if (!name) {
    return res.status(400).json({ success: false, message: 'className 不能为空' });
  }
  if (!customSchedule || typeof customSchedule !== 'object') {
    return res.status(400).json({ success: false, message: 'customSchedule 不能为空' });
  }
  const planTitle = String(customSchedule.planTitle || '').trim();
  const startDayEpoch = Number(customSchedule.startDayEpoch);
  const endDayEpoch = Number(customSchedule.endDayEpoch);
  const events = Array.isArray(customSchedule.events) ? customSchedule.events : [];
  if (!planTitle) {
    return res.status(400).json({ success: false, message: 'customSchedule.planTitle 不能为空' });
  }
  if (!Number.isFinite(startDayEpoch) || !Number.isFinite(endDayEpoch) || events.length === 0) {
    return res.status(400).json({ success: false, message: 'customSchedule 数据不完整（日期或事件为空）' });
  }

  const file = classPlansFilePath(name);
  const existing = readJsonFileOrDefault(file, { className: name, plans: [] });
  const plans = Array.isArray(existing.plans) ? existing.plans : [];
  const id = Date.now().toString(36) + Math.random().toString(36).substring(2, 8);
  const item = {
    id,
    createdAt: Date.now(),
    customSchedule: {
      planTitle,
      startDayEpoch,
      endDayEpoch,
      events
    }
  };
  plans.push(item);
  // 只保留最近 100 条发布，避免文件无限膨胀
  const trimmedPlans = plans.slice(-100);
  writeJsonFile(file, { className: name, plans: trimmedPlans });

  res.json({ success: true, className: name, id, total: trimmedPlans.length });
});

app.get('/api/classPlans', (req, res) => {
  const name = String((req.query && req.query.className) || '').trim();
  if (!name) {
    return res.status(400).json({ success: false, message: '缺少 className 参数' });
  }
  const limit = Math.max(1, Math.min(Number(req.query.limit || 50), 100));
  const file = classPlansFilePath(name);
  const existing = readJsonFileOrDefault(file, { className: name, plans: [] });
  const plans = Array.isArray(existing.plans) ? existing.plans : [];
  const list = plans.slice(-limit).reverse(); // 最新在前
  res.json({ success: true, className: name, plans: list });
});

/**
 * POST /api/requestSmsCode
 * body: { phone: "138xxxxxx" }
 * 行为：生成 6 位验证码，保存在内存中并打印到控制台；真实环境可在此处对接短信平台。
 */
app.post('/api/requestSmsCode', (req, res) => {
  const { phone } = req.body || {};

  if (!phone || !/^\d{11}$/.test(phone)) {
    return res.status(400).json({
      success: false,
      message: '手机号格式不正确'
    });
  }

  const code = generateCode();
  const expireAt = Date.now() + CODE_EXPIRE_MS;
  smsStore[phone] = { code, expireAt };
  lastSmsCodeRecord = {
    phoneMasked: String(phone).replace(/^(\d{3})\d{4}(\d{4})$/, '$1****$2'),
    code,
    expireAt,
    createdAt: Date.now()
  };

  console.log(`[SMS] 为手机号 ${phone} 生成验证码 ${code}，有效期 5 分钟。`);

  res.json({
    success: true,
    message: '验证码已生成（教学环境下请查看服务器控制台或 debugCode）',
    // 为方便教学与调试，这里直接返回验证码；真实环境下应删除该字段
    debugCode: code
  });
});

/**
 * POST /api/verifySmsCode
 * body: { phone: "138xxxxxx", code: "123456" }
 * 行为：校验验证码是否正确且未过期。
 */
app.post('/api/verifySmsCode', (req, res) => {
  const { phone, code } = req.body || {};

  if (!phone || !code) {
    return res.status(400).json({
      success: false,
      message: '手机号和验证码均为必填'
    });
  }

  const record = smsStore[phone];
  if (!record) {
    return res.status(400).json({
      success: false,
      message: '该手机号尚未请求验证码或验证码已失效'
    });
  }

  if (Date.now() > record.expireAt) {
    delete smsStore[phone];
    return res.status(400).json({
      success: false,
      message: '验证码已过期，请重新获取'
    });
  }

  if (record.code !== code) {
    return res.status(400).json({
      success: false,
      message: '验证码错误'
    });
  }

  // 验证通过后删除，避免重复使用
  delete smsStore[phone];

  res.json({
    success: true,
    message: '验证码验证通过'
  });
});

app.listen(PORT, () => {
  console.log(`Backup server running at http://localhost:${PORT}`);
});


