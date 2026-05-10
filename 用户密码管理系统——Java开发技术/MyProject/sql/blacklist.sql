CREATE TABLE blacklist (
    blacklistid INT PRIMARY KEY AUTO_INCREMENT,
    userid INT NOT NULL,
    FOREIGN KEY (userid) REFERENCES user(userid)
); 