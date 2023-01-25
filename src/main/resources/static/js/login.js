const fields = document.getElementsByClassName("field");
const submitBtn = document.getElementById("submit");

function onInputChange() {
    let flag = false;
    for (let field of fields) {
        flag = field.value.length === 0;
        if (flag)
            break;
    }
    submitBtn.disabled = flag;
}

onInputChange();
for (let field of fields)
    field.addEventListener("input", onInputChange);