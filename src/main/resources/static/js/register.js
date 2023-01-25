document.getElementById("main-form").onsubmit = (event) => {
    event.preventDefault();
    const formData = new FormData(event.target);
    console.log(formData);
    const data = {
        username: formData.get("username"),
        password: formData.get("password")
    }

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/api/public/user/register", true);
    xhr.setRequestHeader("Content-Type", "application/json");
    xhr.onload = (event) => {
        if (event.target.status === 200)
            window.location = "/login";
    }
    xhr.send(JSON.stringify(data));
}

const fields = document.getElementsByClassName("field");
const passwordField = document.getElementById("password");
const confirmPasswordField = document.getElementById("confirm-password");
const submitButton = document.getElementById("submit");

function onInputChange() {
    let flag = passwordField.length === 0 || passwordField.value !== confirmPasswordField.value;
    if (!flag) {
        for (let field of fields) {
            flag = field.value.length === 0;
            if (flag)
                break;
        }
    }
    submitButton.disabled = flag;
}

onInputChange();
for (let field of fields)
    field.addEventListener("input", onInputChange);