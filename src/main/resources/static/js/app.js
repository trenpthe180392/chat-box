// app.js — client STOMP over SockJS
(() => {
    let stompClient = null;

    const $ = (id) => document.getElementById(id);
    const statusEl = $("status");
    const chatEl = $("chat");
    const nameEl = $("name");
    const msgEl = $("message");
    const formEl = $("form");
    const connectBtn = $("connectBtn");
    const disconnectBtn = $("disconnectBtn");
    const sendBtn = $("sendBtn");

    function setStatus(connected){
        statusEl.textContent = connected ? "Connected" : "Disconnected";
        statusEl.className = `badge ${connected ? "badge--connected" : "badge--disconnected"}`;
        connectBtn.disabled = connected;
        disconnectBtn.disabled = !connected;
        sendBtn.disabled = !connected;
        msgEl.disabled = !connected;
    }

    function appendMessage({ from = "Anonymous", content = "", timestamp = null }){
        const wrapper = document.createElement("div");
        wrapper.className = "message";

        const meta = document.createElement("div");
        meta.className = "meta";

        const who = document.createElement("span");
        who.className = "from";
        who.textContent = from;

        const time = document.createElement("span");
        const t = timestamp ? new Date(timestamp) : new Date();
        time.textContent = " • " + t.toLocaleString();

        meta.appendChild(who);
        meta.appendChild(time);

        const body = document.createElement("div");
        body.className = "content";
        body.textContent = content;

        wrapper.appendChild(meta);
        wrapper.appendChild(body);

        chatEl.appendChild(wrapper);
        chatEl.scrollTop = chatEl.scrollHeight;
    }

    function appendSystem(text, isError=false){
        appendMessage({ from: isError ? "System (Error)" : "System", content: text });
    }

    function connect(){
        const socket = new SockJS("/ws");
        stompClient = Stomp.over(socket);
        // Tắt log STOMP nếu muốn gọn console:
        // stompClient.debug = null;

        stompClient.connect({}, () => {
            setStatus(true);
            appendSystem("Đã kết nối.");

            // Nhận tin nhắn chat
            stompClient.subscribe("/topic/public", (frame) => {
                const msg = JSON.parse(frame.body);
                appendMessage(msg);
            });

            // Nhận lỗi từ server (nếu bạn đã bật @MessageExceptionHandler -> /topic/errors)
            stompClient.subscribe("/topic/errors", (frame) => {
                appendSystem(frame.body, true);
            });
        }, (err) => {
            setStatus(false);
            appendSystem("Mất kết nối: " + (err?.body || err), true);
        });
    }

    function disconnect(){
        if (stompClient) {
            stompClient.disconnect(() => {
                setStatus(false);
                appendSystem("Đã ngắt kết nối.");
            });
            stompClient = null;
        } else {
            setStatus(false);
        }
    }

    function sendMessage(e){
        e.preventDefault();
        const from = (nameEl.value || "").trim() || "Anonymous";
        const content = (msgEl.value || "").trim();
        if (!content) return;

        const payload = { from, content };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(payload));
        msgEl.value = "";
        msgEl.focus();
    }

    // Event bindings
    document.addEventListener("DOMContentLoaded", () => {
        setStatus(false);
        msgEl.disabled = true;

        connectBtn.addEventListener("click", connect);
        disconnectBtn.addEventListener("click", disconnect);
        formEl.addEventListener("submit", sendMessage);
    });
})();
