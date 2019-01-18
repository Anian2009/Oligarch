$(document).ready(function () {

    console.log("Hello main.js");

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
        let cardData;
        data.forEach(function (item) {
            cardData = '<div class="card text-white bg-dark mb-3">';
            cardData += '<img class="card-img-top" height="200" src=' + item.fabric.img + ' alt=' + item.fabric.img + '>';
            cardData += '<div class="card-body">';
            cardData += '<h5 class="card-title"> ' + item.fabric.fabricName + ' :</h5>';
            cardData += '<div class="card-text">mining per second - ' + item.miningPerSecond + ' ;</div>';
            cardData += '<div class="card-text">level - ' + item.fabricLevel + ' ;</div>';
            cardData += '<div class="card-text">upgrade price - ' + item.fabric.upgrade + ' ;</div>';
            cardData += '<p></p><a id="' + item.id + '" name="up' + item.id + '" class="btn btn-primary">Upgrade</a>';
            cardData += '</div>';
            cardData += '</div>';
            $('#user-card').append(cardData);
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
        error: function (jqXHR){
          console.log(jqXHR.status+" "+jqXHR.responseText);
        },
    });

    $('#logOut').click(function () {
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
                alert("Congratulations! \n " +
                    "From now all your mining will come in gold coins. " +
                    "At any time, you can exchange gold coins for silver at an advantageous rate.");
            },
            error: function (jqXHR){
                alert(jqXHR.status+" "+jqXHR.responseText);
            },
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
            amount: +sessionStorage.getItem("price"),
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

    $('#goldSellGold').change(function () {
        if ($('#goldSellGold').val() > goldCoins) {
            alert("You're not as rich as you want.");
            $('#goldSellGold').val(goldCoins);
        }
        $('#silverSellGold').val($('#goldSellGold').val() * sessionStorage.getItem("rateGold"));
    });

    $('#silverBuyGold').change(function () {
        if ($('#silverBuyGold').val() > silverCoins) {
            alert("You're not as rich as you want.");
            $('#silverBuyGold').val(silverCoins);
        }
        $('#goldBuyGold').val($('#silverBuyGold').val() / sessionStorage.getItem("rateSilver"));
    });

    $('#exchangeSellGold').click(function () {
        $.ajax('/api/user/exchange', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {
                myGoldCoins: - $('#goldSellGold').val(),
                mySilverCoins: $('#silverSellGold').val(),
                id: sessionStorage.getItem("id")},
            success: function (data) {
            },
            error: function (jqXHR) {
                console.log(jqXHR.toString());
            }
        });
        $('#goldSellGold').val(0);
        $('#silverSellGold').val(0);
    });

    $('#exchangeBuyGold').click(function () {
        $.ajax('/api/user/exchange', {
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: {
                mySilverCoins: - $('#silverBuyGold').val(),
                myGoldCoins: $('#goldBuyGold').val(),
                id: sessionStorage.getItem("id")},
            success: function (data) {
            },
            error: function (jqXHR) {
                console.log(jqXHR.toString());
            }
        });
        $('#goldBuyGold').val(0);
        $('#silverBuyGold').val(0);
    });
});