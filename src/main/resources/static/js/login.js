$(document).ready(function () {
    console.log("Hello login.js");

    $('#login').click(function () {
        $.ajax("/api/guest/log-in", {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            data: {
                email: $('#lInputEmail').val(),
                password: $('#lInputPassword').val()
            },
            success: function (data) {
                sessionStorage.setItem("token", data.message);
                sessionStorage.setItem("id", data.id);
                sessionStorage.setItem("role", data.role);
                sessionStorage.setItem("email", data.email);
                sessionStorage.setItem("rateSilver", data.rateSilver);
                sessionStorage.setItem("rateGold", data.rateGold);
                sessionStorage.setItem("price", data.price);
                if (data.role === "ADMIN")
                    window.location = '../admin/addFabric.html';
                else
                    window.location = '../user/dashboard.html';
                },
            error: function (jqXHR) {
                alert(jqXHR.responseJSON.status +" - "+jqXHR.responseJSON.message);
            }
        })
    });
});