'use strict';

const socket = new SockJs("/ws")
const stompClient = Stomp.over(socket);
const messageList = document.getElementById("message-list");

function onMessageReceived(payload) {
    console.log(payload);
}

function onConnected() {
    stompClient.subscribe("/topic/", onMessageReceived);
}

function onError() {
    console.log("error");
}

stompClient.connect({}, onConnected, onError);
