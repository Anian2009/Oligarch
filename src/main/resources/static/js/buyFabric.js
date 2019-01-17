$(document).ready(function () {

    console.log("Hello buyFabric.js");

    let userInafo = function (data) {
        document.getElementById('user_name').innerHTML = '<text>' + data.name + ':</text>';
        document.getElementById('silver_bal').innerHTML = '<text>Silver balance - ' + data.silverBalance + ';</text>';
        document.getElementById('gold_bal').innerHTML = '<text>Gold balance - ' + data.goldBalance + ';</text>';
        document.getElementById('incrise').innerHTML = '<text>Increase per second - ' + data.increase + ';</text>';
    };

    let usersInafo = function (data) {
        $('#users_list').html('');
        let list;
        data.forEach(function (item) {
            list = '<li>';
            list += item.name + ' - ';
            list += item.silverBalance + ';';
            list += '</li>';
            $('#users_list').append(list);
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
                console.log(jqXHR.toString());
            }
        });
    };

    getUsersInfo();

    let fabricsList = function (data) {
        data.fabrics.forEach(function (item) {
            let emploee_data = '';
            emploee_data += '<tr>';
            emploee_data += '<td align="left">' + item.fabricName + '</td>';
            emploee_data += '<td align="center">' + item.price + '</td>';
            emploee_data += '<td align="center">' + item.upgrade + '</td>';
            emploee_data += '<td align="center">' + item.miningPerSecond + '</td>';
            emploee_data += '<td align="center"><button id="' + item.id + '" name="bu' + item.id + '" type="button" class="btn btn-primary btn-sm">Buy</button>';
            emploee_data += '</tr>';
            $('#factories_to_buy').append(emploee_data);
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
            alert(jqXHR);
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

    $('#log_out').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

    setInterval(function () {
        getUsersInfo();
    }, 1000);
});