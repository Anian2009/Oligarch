$(document).ready(function () {

    console.log("Hello main-1.js");

    let goldCoins;
    let silverCoins;

    let userInafo = function (data) {
        document.getElementById('userName').innerHTML = '<text>' + data.name + ':</text>';
        document.getElementById('silverBal').innerHTML = '<text>Silver balance - ' + data.silverBalance + ';</text>';
        document.getElementById('goldBal').innerHTML = '<text>Gold balance - ' + data.goldBalance + ';</text>';
        document.getElementById('increase').innerHTML = '<text>Increase per second - ' + data.increase + ';</text>';
        goldCoins = Math.trunc(data.goldBalance);
        silverCoins = Math.trunc(data.silverBalance);
    };

    let usersInafo = function (data) {
        $('#usersList').html('');
        let list;
        data.forEach(function (item) {
            list = '<li>';
            list += item.name + ' - ';
            list += item.silverBalance + ';';
            list += '</li>';
            $('#usersList').append(list);
        })
    };

    let getUsersInfo = function () {
        $.ajax('/api/user/dashboard', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {id: sessionStorage.getItem("id")},
            success: function (data) {
                userInafo(data.user);
                usersInafo(data.users)
            },
            error: function (jqXHR) {
                console.log(jqXHR.status + " : "+jqXHR.statusText);
            }
        });
    };

    $('#logOut').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

    setInterval(function () {
        getUsersInfo();
    }, 1000);

    getUsersInfo();
});