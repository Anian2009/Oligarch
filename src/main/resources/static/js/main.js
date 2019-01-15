$(document).ready(function () {

    console.log("Hello main.js");

    let goldCoins;

    let userInafo = function (data) {
        document.getElementById('user_name').innerHTML = '<text>' + data.name + ':</text>';
        document.getElementById('silver_bal').innerHTML = '<text>Silver balance - ' + data.silver_balance + ';</text>';
        document.getElementById('gold_bal').innerHTML = '<text>Gold balance - ' + data.gold_balance + ';</text>';
        document.getElementById('incrise').innerHTML = '<text>Increase per second - ' + data.incrice + ';</text>';
        goldCoins = Math.trunc(data.gold_balance);
    };

    let usersInafo = function (data) {
        $('#users_list').html('');
        let list;
        data.forEach(function (item) {
            list = '<li>';
            list += item.name + ' - ';
            list += item.silver_balance + ';';
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

    function upgradeThisFabric(id) {
        $.ajax('/api/user/upgrade-factory', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {id: id},
            success: function (data) {
                if (data.message === "Abort")
                    alert("Unfortunately, you do not have the funds to upgrade your factory.");
                else {
                    showMyFabric(data.fabrics);
                    userInafo(data.fabrics[0].master);
                    alert("Congratulations! You upgraded the plant. Your profit is increasing.");
                }
            }
        })
    }

    function showMyFabric(data) {
        $('#user-card').html('');
        let card_data;
        data.forEach(function (item) {
            card_data = '<div class="card text-white bg-dark mb-3">';
            card_data += '<img class="card-img-top" height="200" src=' + item.fabric.img + ' alt=' + item.fabric.img + '>';
            card_data += '<div class="card-body">';
            card_data += '<h5 class="card-title"> ' + item.fabric.fabric_name + ' :</h5>';
            card_data += '<div class="card-text">mining per second - ' + item.fab_mining_p_s + ' ;</div>';
            card_data += '<div class="card-text">level - ' + item.fabric_leval + ' ;</div>';
            card_data += '<div class="card-text">upgrade price - ' + item.fabric.upgrad + ' ;</div>';
            card_data += '<p></p><a id="' + item.id + '" name="up' + item.id + '" class="btn btn-primary">Upgrade</a>';
            card_data += '</div>';
            card_data += '</div>';
            $('#user-card').append(card_data);
        })
    }

    $(document).on('click', 'a[name^="up"]', function (e) {
        e.preventDefault();
        upgradeThisFabric(this.id);
    });

    $.ajax('/api/user/dashboard', {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        headers: {token: sessionStorage.getItem("token")},
        data: {id: sessionStorage.getItem("id")},
        success: function (data) {
            showMyFabric(data.fabrics);
            userInafo(data.user);
            usersInafo(data.users);
        },
    });

    $('#log_out').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

    // Stripes. card verification; Creating and sending card token to server
    function sendToken(token) {
        $.ajax('/api/user/buy-gold-status', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {stripeToken: token.id, id: sessionStorage.getItem("id")},
            success: function (data) {
                if (data.message === "Ok")
                    alert("Congratulations! \n " +
                        "From now all your mining will come in gold coins. " +
                        "At any time, you can exchange gold coins for silver at an advantageous rate.");
                else
                    alert("Sorry, but your card does not have this amount")
            },
            error: function (e) {
                alert(e.toString());
            }
        });
    }

    var handler = StripeCheckout.configure({
        key: 'pk_test_Kp54gxVu3wLXwB24pYX6Iyf2',
        image: 'https://stripe.com/img/documentation/checkout/marketplace.png',
        locale: 'auto',
        token: function (token) {
            sendToken(token);
        }
    });

    document.getElementById('customButton').addEventListener('click', function (e) {
        // Open Checkout with further options:
        handler.open({
            name: "Anian",//sessionStorage.getItem("username"),
            email: sessionStorage.getItem("email"),
            description: 'Gold status',
            amount: 2000,
        });
        e.preventDefault();
    });

    // Close Checkout on page navigation:
    window.addEventListener('popstate', function () {
        handler.close();
    });

    setInterval(function () {
        getUsersInfo();
    }, 1000);

    $('#gold').change(function () {
        if ($('#gold').val() > goldCoins) {
            alert("You're not as rich as you want.");
            $('#gold').val(goldCoins);
        }
        $('#silver').val($('#gold').val() * 100);
    });

    $('#exchange').click(function () {
        $.ajax('/api/user/exchange', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {coins: $('#gold').val(), id: sessionStorage.getItem("id")},
            success: function (data) {
            },
            error: function (jqXHR) {
                console.log(jqXHR.toString());
            }
        });
        $('#gold').val(0);
        $('#silver').val(0);
    });
});