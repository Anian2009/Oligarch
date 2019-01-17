$(document).ready(function () {

    console.log("Hello buyFabric.js");

    let userInafo = function (data) {
        document.getElementById('userName').innerHTML = '<text>' + data.name + ':</text>';
        document.getElementById('silverBal').innerHTML = '<text>Silver balance - ' + data.silverBalance + ';</text>';
        document.getElementById('goldBal').innerHTML = '<text>Gold balance - ' + data.goldBalance + ';</text>';
        document.getElementById('increase').innerHTML = '<text>Increase per second - ' + data.increase + ';</text>';
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
                alert(jqXHR.status + " : "+jqXHR.statusText);
            }
        });
    };

    getUsersInfo();

    let fabricsList = function (data) {
        data.fabrics.forEach(function (item) {
            let emploeeData = '';
            emploeeData += '<tr>';
            emploeeData += '<td align="left">' + item.fabricName + '</td>';
            emploeeData += '<td align="center">' + item.price + '</td>';
            emploeeData += '<td align="center">' + item.upgrade + '</td>';
            emploeeData += '<td align="center">' + item.miningPerSecond + '</td>';
            emploeeData += '<td align="center"><button id="' + item.id + '" name="bu' + item.id + '" type="button" class="btn btn-primary btn-sm">Buy</button>';
            emploeeData += '</tr>';
            $('#factoriesToBuy').append(emploeeData);
        })
    };

    $.ajax('/api/user/factory-market', {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        headers: {token: sessionStorage.getItem("token")},
        success: function (data) {
            fabricsList(data);
        },
        error: function (jqXHR) {
            alert(jqXHR.status + " : "+jqXHR.statusText);
        }
    });

    let buyFabric = function (id) {
        $.ajax('/api/user/buy-factory', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {id: id, userID: sessionStorage.getItem("id")},
            success: function (data) {
                alert("Congratulations! You have become the owner of a new plant. Information about your factories is on the main page.");
            },
            error: function (jqXHR){
                alert("Unfortunately, you do not have enough money to buy the selected plant.");
            }
        })
    };

    $(document).on('click', 'button[name^="bu"]', function (e) {
        e.preventDefault();
        buyFabric(this.id);
    });

    $('#logOut').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

    setInterval(function () {
        getUsersInfo();
    }, 1000);
});