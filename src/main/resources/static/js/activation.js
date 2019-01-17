$(document).ready(function () {

    console.log("Hello activation.js");

    var code = window.location.toString().split("code=")[1];

    $.ajax("/activation-code", {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        data: {code: code},
        success: function (data) {
            $('#message').html("You have successfully completed the registration and you can log into the game using your name and password.");
        },
        error: function (jqXHR) {
            $('#message').html("Sorry something went wrong. Repeat registration will help resolve this error.");
        }
    })
});