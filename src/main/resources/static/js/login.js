$(document).ready(function () {
    console.log("Hello login.js");

    $('#login').click(function () {
        $.ajax("/api/guest/log-in", {
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify({
                name: $('#lInputName').val(),
                password: $('#lInputPassword').val()
            }),
            success: function (data) {
                if (data.message === "AbortNameOrPassword") {
                    alert("Login or password entered incorrectly.");
                    $('#lInputName').val("");
                    $('#lInputPassword').val("");
                } else {
                    if (data.message === "AbortCode") {
                        alert("You have not completed the registration. Check your email.")
                    } else {
                        sessionStorage.setItem("token", data.message);
                        sessionStorage.setItem("id", data.id);
                        sessionStorage.setItem("role", data.role);
                        sessionStorage.setItem("email", data.email);
                        if (data.role === "ADMIN")
                            window.location = '../admin/addFabric.html';
                        else
                            window.location = '../user/dashboard.html';
                    }
                }
            },
            error: function (jqXHR) {
                alert("Fuck!!!")
            }
        })
    });
});