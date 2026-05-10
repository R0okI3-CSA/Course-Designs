function pad2(n) {
  return String(n).padStart(2, '0');
}

function formatTime(ts) {
  const d = new Date(ts);
  return `${pad2(d.getHours())}:${pad2(d.getMinutes())}:${pad2(d.getSeconds())}`;
}

function formatDateTime(ts) {
  const d = new Date(ts);
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())} ${pad2(d.getHours())}:${pad2(d.getMinutes())}:${pad2(d.getSeconds())}`;
}

function formatDurationSec(sec) {
  sec = Math.max(0, Number(sec || 0));
  const h = Math.floor(sec / 3600);
  const m = Math.floor((sec % 3600) / 60);
  const s = Math.floor(sec % 60);
  if (h > 0) return `${h}小时${m}分${s}秒`;
  if (m > 0) return `${m}分${s}秒`;
  return `${s}秒`;
}

function formatMs(ms) {
  ms = Number(ms || 0);
  if (ms < 1000) return `${ms}ms`;
  return `${(ms / 1000).toFixed(2)}s`;
}

function getToken() {
  // 若你设置了 ADMIN_TOKEN，可以在 URL 上带 ?token=xxx；页面会自动带上该 token 请求接口
  const url = new URL(window.location.href);
  return url.searchParams.get('token') || '';
}

async function fetchJson(path) {
  const token = getToken();
  const url = new URL(path, window.location.origin);
  if (token) url.searchParams.set('token', token);
  const res = await fetch(url.toString(), {
    headers: token ? { 'x-admin-token': token } : {}
  });
  if (!res.ok) {
    const t = await res.text();
    throw new Error(`HTTP ${res.status}: ${t}`);
  }
  return await res.json();
}

async function postJson(path, body) {
  const token = getToken();
  const url = new URL(path, window.location.origin);
  if (token) url.searchParams.set('token', token);
  const res = await fetch(url.toString(), {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json; charset=utf-8',
      ...(token ? { 'x-admin-token': token } : {})
    },
    body: JSON.stringify(body || {})
  });
  if (!res.ok) {
    const t = await res.text();
    throw new Error(`HTTP ${res.status}: ${t}`);
  }
  return await res.json();
}

function setText(id, text) {
  const el = document.getElementById(id);
  if (el) el.textContent = text;
}

function renderBackups(backups) {
  const tbody = document.querySelector('#backupTable tbody');
  tbody.innerHTML = '';
  backups.slice(0, 30).forEach(b => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${escapeHtml(b.userId)}</td>
      <td>${escapeHtml(b.sizeText || String(b.sizeBytes || 0))}</td>
      <td>${b.mtimeMs ? escapeHtml(formatDateTime(b.mtimeMs)) : '-'}</td>
    `;
    tbody.appendChild(tr);
  });
}

function renderRequests(items) {
  const tbody = document.querySelector('#reqTable tbody');
  tbody.innerHTML = '';
  items.slice(0, 60).forEach(it => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${escapeHtml(formatTime(it.time))}</td>
      <td>${escapeHtml(it.method)}</td>
      <td>${escapeHtml(it.path)}</td>
      <td>${escapeHtml(String(it.status))}</td>
      <td>${escapeHtml(formatMs(it.durationMs))}</td>
    `;
    tbody.appendChild(tr);
  });
}

function renderSms(items) {
  const tbody = document.querySelector('#smsTable tbody');
  tbody.innerHTML = '';
  items.slice(0, 30).forEach(it => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${escapeHtml(it.phoneMasked)}</td>
      <td>${escapeHtml(formatDurationSec(Math.ceil((it.expiresInMs || 0) / 1000)))}</td>
      <td>${escapeHtml(formatDateTime(it.expireAt))}</td>
    `;
    tbody.appendChild(tr);
  });
}

function escapeHtml(s) {
  return String(s ?? '')
    .replaceAll('&', '&amp;')
    .replaceAll('<', '&lt;')
    .replaceAll('>', '&gt;')
    .replaceAll('"', '&quot;')
    .replaceAll("'", '&#039;');
}

function dateToEpochDay(dateStr) {
  // dateStr: YYYY-MM-DD
  const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(dateStr || '');
  if (!m) return null;
  const y = Number(m[1]);
  const mo = Number(m[2]);
  const d = Number(m[3]);
  const utcMs = Date.UTC(y, mo - 1, d);
  return Math.floor(utcMs / 86400000);
}

function timeToMinutes(t) {
  // t: HH:MM
  const m = /^(\d{2}):(\d{2})$/.exec(t || '');
  if (!m) return null;
  const hh = Number(m[1]);
  const mm = Number(m[2]);
  return hh * 60 + mm;
}

function setMsg(text, isError) {
  const el = document.getElementById('pubMsg');
  if (!el) return;
  el.textContent = text || '';
  el.style.color = isError ? 'rgba(244, 67, 54, 0.95)' : 'rgba(233, 238, 252, 0.7)';
}

function buildEventItemHtml(index) {
  return `
    <div class="event-item" data-event>
      <div class="event-head">
        <div class="event-title">事件 #${index + 1}</div>
        <button type="button" class="btn danger" data-remove>删除</button>
      </div>
      <div class="row">
        <div class="col">
          <label class="label">日期</label>
          <input class="input" type="date" data-date />
        </div>
        <div class="col">
          <label class="label">开始时间</label>
          <input class="input" type="time" value="20:00" data-start />
        </div>
        <div class="col">
          <label class="label">结束时间</label>
          <input class="input" type="time" value="21:00" data-end />
        </div>
      </div>
      <label class="label">事件标题</label>
      <input class="input" placeholder="例如：高数期末冲刺" data-title />
      <label class="label">事件内容</label>
      <textarea class="textarea" rows="3" placeholder="填写学习任务内容" data-content></textarea>
    </div>
  `;
}

function renumberEvents() {
  const items = Array.from(document.querySelectorAll('#eventsContainer [data-event]'));
  items.forEach((el, idx) => {
    const title = el.querySelector('.event-title');
    if (title) title.textContent = `事件 #${idx + 1}`;
  });
}

function addEventItem(prefill) {
  const container = document.getElementById('eventsContainer');
  if (!container) return;
  const index = container.querySelectorAll('[data-event]').length;
  const wrapper = document.createElement('div');
  wrapper.innerHTML = buildEventItemHtml(index);
  const node = wrapper.firstElementChild;
  container.appendChild(node);

  node.querySelector('[data-remove]')?.addEventListener('click', () => {
    node.remove();
    renumberEvents();
  });

  if (prefill) {
    if (prefill.date) node.querySelector('[data-date]').value = prefill.date;
    if (prefill.start) node.querySelector('[data-start]').value = prefill.start;
    if (prefill.end) node.querySelector('[data-end]').value = prefill.end;
    if (prefill.title) node.querySelector('[data-title]').value = prefill.title;
    if (prefill.content) node.querySelector('[data-content]').value = prefill.content;
  }
  renumberEvents();
}

async function loadRecentClassPlansForDisplay() {
  const tbody = document.querySelector('#classPlanTable tbody');
  if (!tbody) return;
  tbody.innerHTML = '';
  const cls = (document.getElementById('pubClassName')?.value || '').trim();
  if (!cls) return;
  try {
    const data = await fetchJson(`/api/classPlans?className=${encodeURIComponent(cls)}&limit=10`);
    const plans = data.plans || [];
    plans.forEach(p => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>${escapeHtml(formatDateTime(p.createdAt || Date.now()))}</td>
        <td>${escapeHtml(cls)}</td>
        <td>${escapeHtml(p.customSchedule?.planTitle || '')}</td>
      `;
      tbody.appendChild(tr);
    });
  } catch (_) {}
}

async function refreshAll() {
  try {
    const health = await fetchJson('/api/admin/health');
    setText('healthStatus', health.ok ? '正常' : '异常');
    setText('healthHint', `now=${formatDateTime(health.now)} | port=${health.port}`);
  } catch (e) {
    setText('healthStatus', '不可用');
    setText('healthHint', String(e.message || e));
  }

  try {
    const overview = await fetchJson('/api/admin/overview');
    setText('uptime', formatDurationSec(overview.uptimeSeconds));
    setText('backupUsers', String(overview.backup?.userBackupCount ?? '—'));
    setText('backupSize', String(overview.backup?.dirSizeText ?? '—'));
    setText('backupDir', String(overview.backup?.dir ?? '—'));
    setText('smsCount', String(overview.sms?.activeCount ?? '—'));
  } catch (e) {
    // overview 失败不影响其它块
  }

  try {
    const latest = await fetchJson('/api/admin/sms/latest');
    if (latest.latest) {
      setText('latestSmsCode', String(latest.latest.code || '—'));
      const exp = latest.latest.expireAt ? formatDateTime(latest.latest.expireAt) : '—';
      const phone = latest.latest.phoneMasked || '—';
      setText('latestSmsHint', `${phone} | 到期：${exp}`);
    } else {
      setText('latestSmsCode', '—');
      setText('latestSmsHint', '暂无');
    }
  } catch (e) {
    // 若设置了 ADMIN_TOKEN 但页面没带 token，会在这里失败
    setText('latestSmsCode', '—');
    setText('latestSmsHint', '无权限或接口不可用');
  }

  try {
    const backups = await fetchJson('/api/admin/backups');
    renderBackups(backups.backups || []);
  } catch (e) {}

  try {
    const reqs = await fetchJson('/api/admin/requests?limit=60');
    renderRequests(reqs.items || []);
  } catch (e) {}

  try {
    const sms = await fetchJson('/api/admin/sms');
    renderSms(sms.items || []);
  } catch (e) {}

  await loadRecentClassPlansForDisplay();
}

document.getElementById('btnRefresh').addEventListener('click', refreshAll);
document.getElementById('pubClassName')?.addEventListener('input', () => {
  loadRecentClassPlansForDisplay();
});

document.getElementById('btnAddEvent')?.addEventListener('click', () => {
  addEventItem();
});

document.getElementById('btnPublish')?.addEventListener('click', async () => {
  setMsg('', false);
  const className = (document.getElementById('pubClassName')?.value || '').trim();
  const planTitle = (document.getElementById('pubPlanTitle')?.value || '').trim();

  if (!className || !planTitle) {
    setMsg('请先填写班级名称与计划标题。', true);
    return;
  }

  const eventNodes = Array.from(document.querySelectorAll('#eventsContainer [data-event]'));
  if (eventNodes.length === 0) {
    setMsg('请至少新增 1 个事件。', true);
    return;
  }

  const events = [];
  let minDay = null;
  let maxDay = null;

  for (let i = 0; i < eventNodes.length; i++) {
    const el = eventNodes[i];
    const dateStr = (el.querySelector('[data-date]')?.value || '').trim();
    const startStr = (el.querySelector('[data-start]')?.value || '').trim();
    const endStr = (el.querySelector('[data-end]')?.value || '').trim();
    const title = (el.querySelector('[data-title]')?.value || '').trim();
    const content = (el.querySelector('[data-content]')?.value || '').trim();

    if (!dateStr || !startStr || !endStr || !title) {
      setMsg(`事件 #${i + 1} 信息不完整（日期/开始/结束/标题必填）。`, true);
      return;
    }
    const day = dateToEpochDay(dateStr);
    const startMin = timeToMinutes(startStr);
    const endMin = timeToMinutes(endStr);
    if (day == null || startMin == null || endMin == null || endMin <= startMin) {
      setMsg(`事件 #${i + 1} 时间不合法（结束时间必须大于开始时间）。`, true);
      return;
    }
    if (minDay == null || day < minDay) minDay = day;
    if (maxDay == null || day > maxDay) maxDay = day;
    events.push({
      dateEpochDay: day,
      startMinutes: startMin,
      endMinutes: endMin,
      title,
      content
    });
  }

  // 排序（同一天按开始时间）
  events.sort((a, b) => (a.dateEpochDay - b.dateEpochDay) || (a.startMinutes - b.startMinutes));

  const customSchedule = {
    planTitle,
    startDayEpoch: minDay,
    endDayEpoch: maxDay,
    events
  };

  try {
    const resp = await postJson('/api/classPlans/publish', { className, customSchedule });
    setMsg(`发布成功：${resp.id}（班级 ${resp.className}）`, false);
    await loadRecentClassPlansForDisplay();
  } catch (e) {
    setMsg(String(e.message || e), true);
  }
});

refreshAll();
setInterval(refreshAll, 5000);

// 默认给一个事件，避免空白
addEventItem();


