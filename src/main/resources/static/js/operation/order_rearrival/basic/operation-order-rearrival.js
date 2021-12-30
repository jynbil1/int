$(document).ready(function () {
    $("#submitBtn").on("click", function () {
        var order_no = $("#rearrivalTxt").val();
        //getAllReArrivalOrderList(order_no);
        validateReArrivalRequest(order_no);
    });

    $("#rearrivalTxt").keydown(function (e) {
        if (e.keyCode == 13) {
            var order_no = $(this).val();
            //getAllReArrivalOrderList(order_no);
            validateReArrivalRequest(order_no);
        }
    });

    $(".back-to-search").on("click", function () {
        $("#searchResult").css("display","none");
        $("#rearrivalDiv").css("display", "block");
    });
});

function getAllReArrivalOrderList(request_no) {
    $.ajax({
        url: '/operation/getAllReArrivalOrderList?request_no=' + request_no,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            $('.loading').css('display', 'none');
            const jsonObject = response.object;
            var count = jsonObject.count;
            var message = jsonObject.message;
            if (count > 1 && count != null) {
                var tr = '';
                $.each(jsonObject.items, function (i, object) {
                    var tracking_no = object.tracking_no == null ? "미발급" : object.tracking_no;
                    var processed = object.processed == true ? "Processed" : "Ongoing";
                    var btn = "";

                    if (object.processed != true) {
                        btn = '<span class="rearrival-update"><a href="/operation/order-rearrival-update?order_no=' + object.order_no + '">Update</a></span>';
                    }
                    
                    tr += '<tr>'
                        + '<td class="shipment-date">' + object.shipment_date + '</td>'
                        + '<td class="order-no">' + object.order_no + '</td>'
                        + '<td class="tracking-no">' + tracking_no + '</td>'
                        + '<td class="sku">' + object.sku + '</td>'
                        + '<td class="order-status">' + processed + '</td>'
                        + '<td class="update">' + btn + '</td>';
                });
                $('tbody#productList').html(tr);
                $('#rearrivalTable').css('display','block');
                $('#rearrivalDiv').css('display', 'none');
                $('.loading').css('display', 'none');
            } else if (count == 1 && count != null) {
                $.each(jsonObject.items, function (i, object) {
                    window.location.href = "/operation/order-rearrival-update?order_no=" + object.order_no;
                });
                $('.loading').css('display', 'none');
            } else {
                console.log(count);
                $('.loading').css('display', 'none');
                $("#searchResult").css("display", "block");
                $("#rearrivalDiv").css("display", "none");
                $('h2.description').html(message);
            }
        },
        error: function () {
            $('.loading').css('display', 'none');
            $(".error-msg").html("An error has occurred.").fadeIn(100).css("display", "block").delay(3000).fadeOut(100);
        }
    });
}

function validateReArrivalRequest(request_no) {
    $('.loading').css('display', 'table');
    $.ajax({
        url: '/operation/validateReArrivalRequest?request_no=' + request_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            if (response.code == 200) {
                getAllReArrivalOrderList(request_no);
            } else if (response.code == 405) {
                $('.loading').css('display', 'none');
                $(".error-msg").html(response.message).fadeIn(100).css("display", "block").delay(3000).fadeOut(100);
            } else {
                $('.loading').css('display', 'none');
                $(".error-msg").html(response.message).fadeIn(100).css("display", "block").delay(3000).fadeOut(100);
            }
        },
        error: function() {
            $(".error-msg").html("An error has occurred.").fadeIn(100).css("display", "block").delay(3000).fadeOut(100);
        }
    });
}