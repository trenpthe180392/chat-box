package com.chatbox.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;        // UUID dạng chuỗi
    private String from;      // người gửi
    private String content;   // nội dung
    private Instant timestamp; // thời điểm tạo

    public ChatMessage() {
        this.id = UUID.randomUUID().toString();
        this.timestamp = Instant.now();
    }

    public ChatMessage(String from, String content) {
        this.id = UUID.randomUUID().toString();
        this.from = from;
        this.content = content;
        this.timestamp = Instant.now();
    }

    // getters
    public String getId() { return id; }
    public String getFrom() { return from; }
    public String getContent() { return content; }
    public Instant getTimestamp() { return timestamp; }

    // setters
    public void setId(String id) { this.id = id; }
    public void setFrom(String from) { this.from = from; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", from='" + from + '\'' +
                ", content='" + content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

