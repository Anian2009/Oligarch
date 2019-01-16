$(document).ready(function () {
    console.log("Hello addFabric.js");

    var image;

    function showFabricList(data) {
        data.fabrics.forEach(function (item) {
            var emploee_data = '';
            emploee_data += '<tr>';
            emploee_data += '<td>' + item.fabric_name + '</td>';
            emploee_data += '<td>' + item.price + '</td>';
            emploee_data += '<td>' + item.upgrad + '</td>';
            emploee_data += '<td>' + item.mining_p_s + '</td>';
            emploee_data += '</tr>';
            $('#emploe_table').append(emploee_data);
        })
    }

    $.ajax('/api/admin/factory-list', {
        type: 'GET',
        dataType: 'json',
        contentType: 'application/json',
        headers: {token: sessionStorage.getItem("token")},
        success: function (data) {
            showFabricList(data);
        },
        error: function (jqXHR) {
            alert("FUCK!!!");
        }
    });

    $('#add_fabric').click(function () {
        var p, n, u, m, i;

        if ((p = $('#new_price').val()) === null || p === "") {
            alert("Any factory is worth something!");
            return false;
        }

        if ((n = $('#new_name').val()) === null || n === "") {
            alert("How do you call the plant - so it will work.");
            return false
        }
        if ((u = $('#new_upgrad').val()) === null || u === "") {
            alert("Any equipment worth the money.");
            return false
        }

        if ((m = $('#new_mining').val()) === null || m === "") {
            alert("Why do we need the plant - which does not make a profit?");
            return false
        }

        if ((i = $('#new_image').val()) === null || i === "") {
            alert("Selected a factory photo.");
            return false
        }

        console.log("Post zapros start");
        console.log(image);

        $.ajax('/api/admin/add-factory', {
            type: 'POST',
            dataType: 'json',
            contentType: 'application/json',
            headers: {token: sessionStorage.getItem("token")},
            data: JSON.stringify({
                newPrice: p,
                newName: n,
                newUpgrade: u,
                newMining: m,
                image: i,
            }),
            success: function (data) {

                var emploee_data = '';
                emploee_data += '<tr>';
                emploee_data += '<td>' + $('#new_name').val() + '</td>';
                emploee_data += '<td>' + $('#new_price').val() + '</td>';
                emploee_data += '<td>' + $('#new_upgrad').val() + '</td>';
                emploee_data += '<td>' + $('#new_mining').val() + '</td>';
                emploee_data += '</tr>';
                $('#emploe_table').append(emploee_data);

                $('#new_image').html("");
                $('#new_price').val("");
                $('#new_name').val("");
                $('#new_upgrad').val("");
                $('#new_mining').val("");

            },
            error: function (jqXHR) {
                alert("FUCK!!!");
            }
        });
    })

    $(document).on('click', 'img[name^="im"]', function (e) {
        e.preventDefault();
        $('#new_image').val(this.id);
        $('#new_im').html('');
        $('#new_im').append('<img width="70" height="50" src=' + this.id + ' className="rounded" />');
    });

    $('#log_out').click(function () {
        sessionStorage.clear();
        window.location = '../index.html';
    });

});