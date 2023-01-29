'use strict';

const conversationId = window.location.href.substring(
    window.location.href.lastIndexOf("/") + 1
);
const socket = new SockJS("/ws")
const stompClient = Stomp.over(socket);
const messageList = document.getElementById("message-list");

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);

    // fetching the user
    const xhr = new XMLHttpRequest();
    xhr.open("GET", `/api/public/user?userId=${message.senderId}`, true);
    xhr.onload = (event) => {
        const sender = JSON.parse(event.target.response);
        const messageElement = document.createElement("li");
        const messageContainer = document.createElement("div");
        const username = document.createElement("h3");
        username.innerText = sender.username;
        messageContainer.appendChild(username);
        const content = document.createElement("p");
        content.innerText = message.content;
        messageContainer.appendChild(content);
        messageElement.appendChild(messageContainer);
        messageList.appendChild(messageElement);
    }
    xhr.send(null);
}

function onConnected() {
    stompClient.subscribe("/topic/" + conversationId, onMessageReceived);
}

function onError() {
    console.error("error");
}

function sendTextMessage(content) {
    content = content.trim();
    if (stompClient) {
        stompClient.send(
            "/app/chat/" + conversationId,
            { },
            JSON.stringify({ type: "TEXT", content: content })
        );
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
    contentInput.value = "";
}
