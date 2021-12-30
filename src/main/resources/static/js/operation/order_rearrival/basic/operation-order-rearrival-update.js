var orderNo, trackingNo;

$(document).ready(function () {
    orderNo = getOrderNoParam('order_no');
    getOrderReArrivalInspectionDetailVO(orderNo);
    getOrderReArrivalInspectionVO(orderNo);

    $("#processRearrival").on("click", function () {
        var is_staggered = false;
        showPasscodePopup(is_staggered, "");
    });
});

function getOrderNoParam(order_no) {
    var pageUrl = window.location.search.substring(1);
    var urlVar = pageUrl.split('&');

    for (var i = 0; i < urlVar.length; i++) {
        var paramName = urlVar[i].split('=');
        if (paramName[0] == order_no) {
            return paramName[1];
        }
    }
}

function getOrderReArrivalInspectionDetailVO(order_no) {
    $.ajax({
        url: '/operation/getOrderReArrivalInspectionDetailVO?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            var tracking_no = jsonObject.tracking_no == null ? "미발급" : jsonObject.tracking_no;
            trackingNo = tracking_no;
            tr += '<tr><td class="order-no"># ' + order_no + '</td>' +
                '<td>' + tracking_no + '</td>' +
                '<td>주문 일자: ' + formatDate(jsonObject.order_date) + '</td>' +
                '<td>출고 일자: ' + formatDate(jsonObject.forwarding_date) + '</td></tr>' +
                '<tr><td>고객명: ' + jsonObject.customer_name + '</td>' +
                '<td>E-mail: ' + jsonObject.email + '</td>' +
                '<td>주문액: ' + jsonObject.total_amount + '</td>' +
                '<td>주문서 상태: <span class="status">' + jsonObject.order_status + '</span></td></tr>';
            $('tbody#prodDetail').html(tr);
        },
        async: true,
        error: function () {
            console.log("error");
        }
    });
}

function getOrderReArrivalInspectionVO(order_no) {
    $.ajax({
        url: '/operation/getOrderReArrivalInspectionVO?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            $.each(jsonObject, function (i, object) {
                tr += '<tr class="product-row"><td class="product-id">' + object.product_id + '</td>' +
                    '<td class="product-name" width="350">' + object.product_name + '</td>' +
                    '<td class="qty">' + object.qty + '</td>' +
                    '<td class="reorder-qty"><input id="insQty" type="number" step="1" max="' + object.qty + '" min="0" value="0" name="quantity" class="quantity-field"></td>' +
                    '<td class="barcode">' + object.barcode + '</td>' +
                    '<td class="expiry-date"><input type="date" id="expDate" name="expiry-date"></td>' +
                    '<td class="update"><span class="rearrival-update" onclick="collectiveStocks(this);">일괄 재입고</span><span class="individual-update" style="margin-left: 15px;">개별재입하</span></td></tr>';
            });
            $('tbody#productList').html(tr);

            $(".individual-update").on("click", function () {
                var order_no = orderNo;
                var product_id = $(this).parent().siblings("td.product-id").text();
                var product_name = $(this).parent().siblings("td.product-name").text();
                var qty = $(this).parent().siblings("td.qty").text();
                var inspected_qty = $(this).parent().siblings("td.reorder-qty").children(".quantity-field").val();
                var expiry_date = $(this).parent().siblings("td.expiry-date").children("#expDate").val();
                var is_staggered = true;

                var processedData = {
                    order_no: order_no,
                    product_id: product_id,
                    product_name: product_name,
                    qty: qty,
                    inspected_qty: inspected_qty,
                    expiry_date: expiry_date
                };
                
                if (expiry_date == null || expiry_date == "") {
                    if (confirm("유통기한 값 없이 처리 하시겠습니까?")) {
                        showPasscodePopup(is_staggered, processedData);
                    }
                } else {
                    showPasscodePopup(is_staggered, processedData);
                }
            });
        },
        async: true,
        error: function () {
            console.log("error");
        }
    });
}

function collectiveStocks(elem) {
    var maxValue = $(elem).parent().siblings('.reorder-qty').find('.quantity-field').attr('max');
    $(elem).parent().siblings('.reorder-qty').find('.quantity-field').val(maxValue);
}

function submitRearrival() {
    $("#passDesc").hide();
    $("#passcodeTxt").hide();
    $("#dataProgress").removeAttr('class');
    $("#dataProgress").addClass('in-progress');
    $("#dataProgress p").html('Please wait, saving in progress..');
    
    var processedData = { "request": [] };

    $.each($("tr.product-row"), function () {
        var order_no = orderNo;
        var product_id = $(this).children("td.product-id").text();
        var product_name = $(this).children("td.product-name").text();
        var qty = $(this).children("td.qty").text();
        var inspected_qty = $(this).children("td.reorder-qty").children(".quantity-field").val();
        var expiry_date = $(this).children("td.expiry-date").children("#expDate").val();
        var jsonSeqNo = {
            'order_no': order_no,
            'product_id': product_id,
            'product_name':product_name,
            'qty': qty,
            'inspected_qty': inspected_qty,
            'expiry_date': expiry_date
        };
        processedData.request.push(jsonSeqNo)
    });
    
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/operation/processReArrival',
        data: JSON.stringify(processedData),
        dataType: "json",
        success: function (response) {
            if (response.code == "200") {
                $("#dataProgress").removeAttr('class');
                $("#dataProgress").addClass('submitted');
                setTimeout(function () {
                    window.location.href = "/operation/order-rearrival";
                }, 2000);
            } else {
                console.log("An error has occurred.");
            }
        }
    });
}

function submitStaggeredRearrival(processedData) {
    $("#passDesc").hide();
    $("#passcodeTxt").hide();
    $("#dataProgress").removeAttr('class');
    $("#dataProgress").addClass('in-progress');
    $("#dataProgress p").html('Please wait, saving in progress..');
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/operation/processStaggeredReArrival',
        data: JSON.stringify(processedData),
        dataType: "json",
        success: function (response) {
            if (response.code == "200") {
                $("#dataProgress").removeAttr('class');
                $("#dataProgress").addClass('submitted');
                $("#dataProgress p").html('Data have been submitted!');
                setTimeout(function () {
                    closePasscodePopup();
                    getOrderReArrivalInspectionVO(orderNo);
                }, 2000);
            } else {
                alert("An error has occurred.");
            }
        }
    });
}

function formatDate(date) {
    var date = new Date(date);
    return [
        date.getFullYear(),
        ('0' + (date.getMonth() + 1)).slice(-2),
        ('0' + date.getDate()).slice(-2)
    ].join('-');
}

function showPasscodePopup(is_staggered, processed_data) {
    $("#passcodePopup").css("visibility", "visible");
    $("#passcodePopup").css("opacity", "1");

    $(".popup-header").html(orderNo + " - " + trackingNo + " 의 재입고 처리를 진행합니다.")
    $("#passcodeTxt").keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            var passcode = $(this).val();
            validatePasscode(passcode, is_staggered, processed_data);
        }
    });
}

function closePasscodePopup() {
    $("#passcodePopup").css("visibility", "hidden");
    $("#passcodePopup").css("opacity", "0");
    $(".popup-header").html("");
    $("#passcodeTxt").val("");
}

function validatePasscode(passcode, is_staggered, processed_data) {
    $.ajax({
        url: '/operation/validateRoutineCompletionCode?code=' + passcode,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: true,
        success: function (response) {
            if (response.code == '200') {
                if (is_staggered == true) {
                    submitStaggeredRearrival(processed_data);
                } else {
                    submitRearrival();
                }
            } else {
                $(".notification-message").fadeIn(1200, function () {
                    $(this).fadeOut(2000);
                });
            }
        },
        error: function () {
            console.log("error");
        }
    });
}