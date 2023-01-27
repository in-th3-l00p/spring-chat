'use strict';

const conversationId = window.location.href.substring(
    window.location.href.lastIndexOf("/") + 1
);
const socket = new SockJS("/ws")
const stompClient = Stomp.over(socket);
const messageList = document.getElementById("message-list");

function onMessageReceived(payload) {
    console.log(payload);
}

function onConnected() {
    stompClient.subscribe("/topic/" + conversationId, onMessageReceived);
}

function onError() {
    console.log("error");
}

function sendTextMessage(content) {
    content = content.trim();
    if (stompClient) {
        stompClient.send(
            "/app/chat/" + conversationId,
            { },
            JSON.stringify({ type: "TEXT", content: content })
        )
    }
}

stompClient.connect({}, onConnected, onError);

const contentInput = document.getElementById("content");
document.getElementById("input-form").onsubmit = (event) => {
    event.preventDefault();
    const input = contentInput.value;
    if (input.length === 0)
        return;
    sendTextMessage(input);
}
