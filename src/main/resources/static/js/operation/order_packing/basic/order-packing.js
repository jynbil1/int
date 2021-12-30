var redirect_;
var summary_details;
var isValidUser;
var downloadTimer;
var zplResponse;
var order_process = 'order_packing';
var isValidRequest;

$(document).ready(function () {

    $('#orderNoSubmit').on("click", function () {
        var i_order_no = $("#fetchLandingPage").val();
        callValidateCheckingUser(i_order_no, order_process);
    }).hide();

    $('#fetchLandingPage').keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            $('#orderNoSubmit').click();
        }
    });

    $("html").click(function () {
        $("#fetchLandingPage").focus();
    });

    $("body").on("click", "#test", function() {
        var file_url = $(this).attr("data-url");
        console.log(file_url);
        printLabel(file_url);
    });

});

function toggleCheckOrderListAndSummary(order_no, order_process) {
    $.ajax({
        url: '/operation/genericValidateOrderNo?order_no=' + order_no + '&order_process=' + order_process,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            console.log(response);
            var code = response.code;
            var status = response.status;
            var message = response.message;

            if (code == '400' || code == '405') {
                getOrderCheckedSummaryVO(order_no, status, message);
                $("div[id=divPackingTable]").show();
                $("div[id='divPackingPage']").hide();
                $("div[id='pageDescription']").hide();
            } else if (code == '200') {
                $("div[id='divPackingPage']").hide();
                $("div[id='pageDescription']").hide();
                if (isValidUser === true) {
                    validatePackingRequest(order_no);
                } else {
                    if (confirm(isValidUser + ' is currently working on this order, which means you cannot make changes, unless you take over.')) {
                        validatePackingRequest(order_no);
                    } else {
                        redirectToOperation();
                        console.log('No changes has been made.');
                    }
                }
            } else if (code == '404') {
                alert("주문 번호가 올바르지 않거나, 존재하지 않습니다");
                $("#fetchLandingPage").val("")
            } else {
                alert("An error has occurred");
                $("#fetchLandingPage").val("")
            }
        },
        error: function () {
            console.log("error");
        }
    });
}

function getOrderPackingDetails(order_no) {
    $.ajax({
        url: '/operation/getOrderPackingDetails?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            if (response.code == '200') {
                const jsonObject = response.object;
                var summaryObject = '';
                var dhlProgress = '';

                summaryObject += '<table class="checkbox-table"><tbody id="orderPackingDetails"><tr>'
                    + '<td class="td-order-packing-no"># ' + jsonObject.order_number + '</td>'
                    + '<td class="td-order-packing-scan"><div><span>발급 송장 바코드 인식</span><input type="text" id="barcodeScanner"></div></td>'
                    + '</tr></tbody></table>'
                    + '<table class="checkbox-table"><tbody id="orderPackingDetails"><tr>'
                    + '<td class="td-order-packing-name">' + jsonObject.full_name + '</td>'
                    + '<td class="td-order-packing-email">' + jsonObject.email + '</td>'
                    + '</tr><tr><td class="td-order-packing-address">' + jsonObject.add_1_2 + ' ' + jsonObject.city_state_postcode
                    + '</tr></tbody></table>'
                    + '<table class="checkbox-table packing-details"><tbody id="shippingDetails1"></tbody></table>'
                    + '<table class="checkbox-table packing-details"><thead><tr><th>권장 가능 박스</th><th>길이 cm</th><th>높이 조절 여부</th></tr></thead><tbody id="shippingDetails2"></tbody></table>'
                    + '<table class="checkbox-table packing-details"><thead><tr><td class="dhl-progress">DHL 송장 발급중...</td></tr></thead><tbody id="trackingNoList"></tbody></table>'
                    + '<div class="table-btn"><button type="submit" class="unshippableOrder button primary" id="unshippableOrder" data-order-no="' + order_no + '">미출고</button>'
                    + '<input type="button" class="button primary" id="reissueInvoice" value="송장 재발급"></div></div>'

                $('#divPackingDetails').html(summaryObject);
                $('#divPackingDetails').css('display', 'block');
                $('#divPackingPage').css('display', 'none');
                $('.description').text('주문서를 포장하는 상태입니다.');
                $('#barcodeScanner').focus();
                getShippingDetails(order_no);

                $("html").off('click').click(function () {
                    $("#barcodeScanner").focus();
                });

                $('#reissueInvoice').on("click", function () {
                    $("td.dhl-progress").html('DHL 송장 발급중...');
                    saveOrderPacking(order_no);
                    getShippingTrackingNoList(order_no);
                });

                $('#barcodeScanner').keypress(function (e) {
                    var key = e.which;
                    if (key == 13) {
                        finalizeOrderPacking(jsonObject.order_number);
                    }
                });

                $('.unshippableOrder').on("click", function() {
                    callValidateCheckingOrderNo($(this).data("order-no"));
                });

                if (isValidRequest) {
                    $('#reissueInvoice').addClass("disabled");
                    $('#reissueInvoice').prop('disabled', true);
                }
            } else {
                alert("An error has occurred.");
            }
        },
        error: function () {
            console.log("error");
        }
    });
}

function saveOrderPacking(order_no) {
    //$('.loading').css('display', 'table');
    $.ajax({
        url: '/operation/saveOrderPackingLabel?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            console.log(response)
            if (response.code == "200") {
                getShippingTrackingNoList(order_no);
                printLabel();
                $('#reissueInvoice').prop('disabled', false);
                $('#reissueInvoice').removeClass('disabled');
                isValidRequest = false;
            } else if (response.code == "401") {
                window.location.href = '/signIn';
            } else if (response.code == "500") {
                console.log(response)
                alert(response.message)
            }
            $("td.dhl-progress").html("DHL 송장 발급 완료.");
            //$('.loading').css('display', 'none');
        },
        error: function () {
            console.log("error");
            //$('.loading').css('display', 'none');
        }
    });
}

function printLabel(fileURL) {
    console.log("start");
    console.log(fileURL);
    var posturl;
    if(fileURL == undefined){
        posturl = '/operation/printOrderPackingLabel'
    }else{
        posturl = '/operation/printOrderPackingLabel?shipping_label_url=' + fileURL
    }
    console.log(posturl)
     $.ajax({
        type: 'POST',
        url: posturl,
        responseType: 'blob',
        success: function (response) {
            var byteCharacters = atob(response);
            console.log(byteCharacters.length)
            var byteNumbers = new Array(byteCharacters.length);
            for (var i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            var byteArray = new Uint8Array(byteNumbers);
            var file = new Blob([byteArray], {type: 'application/pdf;base64'});
            var fileURL = URL.createObjectURL(file);
            var wnd = window.open(fileURL);
            wnd.print();
            setTimeout(function() {
                wnd.close();
            }, 4000);

            $("td.dhl-progress").html("DHL 송장 발급 완료.");
        }
    })
}

function getShippingDetails(order_no) {
    $.ajax({
        url: '/operation/getShippingDetails?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            var tr = ''
            const jsonObject = response.object;
            const box_recommendation = jsonObject.box_recommendation;
            $.each(box_recommendation, function (i, object) {
                tr += '<tr><td>' + object.box_no + '</td>'
                    + '<td>' + object.box_h_up_down + '</td>'
                    + '<td>' + object.box_desc + '</td></tr>'
            });
            $('tbody#shippingDetails1').html('<tr><td>실무게: ' + jsonObject.total_weight + '</td><td>예상 측정 기준: 실무게 기반</td></tr>');
            $('tbody#shippingDetails2').html(tr);
        },
        error: function () {
            console.log("error");
        }
    });
}

function getShippingTrackingNoList(order_no) {
    $.ajax({
        url: '/operation/getShippingTrackingNoList?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            var tr = ''
            const jsonObject = response.object;
            $.each(jsonObject, function (i, object) {
                tr += '<tr><td>DHL Worldwide Express: <a class="tracking-no" id="test" data-url="'+object.shipping_label_url+'">' + object.tracking_no + '</a></td></tr>'
            });
            $('tbody#trackingNoList').html('');
            $('tbody#trackingNoList').html(tr);
            $("td.dhl-progress").html('DHL 송장 발급 완료.');
        },
        error: function () {
            console.log("error");
        }
    });
}

function finalizeOrderPacking(order_no) {
    var tracking_no;
    $('.tracking-no').each(function (index) {
        if (index === 0) {
            tracking_no = $(this).text();
        };
    });

    $('.loading').css('display', 'table');

    if (tracking_no == $("#barcodeScanner").val().trim()) {
        console.log(order_no, tracking_no)
        $.ajax({
            url: '/operation/finalizeOrderPacking?order_no=' + order_no + '&tracking_no=' + tracking_no,
            type: 'POST',
            contentType: "application/json; charset=utf-8",
            success: function (response) {
                $('.loading').css('display', 'none');
                $('#divPackingPage').css('display', 'block');
                $('#divPackingDetails').css('display', 'none');
                $('#divPackingDetails').html('');
                $('#fetchLandingPage').val('').focus();
                $(".notification-msg").html('<i class="fas fa-check-circle"></i>' + order_no + ': ' + tracking_no + ' 주문 포장이 완료되었습니다. 처리자: ' + $("#username").text().replace('[내 정보]', '').trim()).addClass("success");
                $(".notification-message").fadeIn(1200, function () {
                    $(this).fadeOut(2000);
                });
            },
            error: function () {
                console.log("error");
                $('.loading').css('display', 'none');
            }
        });
    } else {
        $('.loading').css('display', 'none');
        alert("Please input the latest tracking number.");
    }
}

function callValidateCheckingUser(order_no, order_process) {
    $.ajax({
        url: '/operation/isCheckingUserValid?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            console.log(JSON.stringify(response));
            if (response.object === true) {
                isValidUser = response.object;
            } else {
                isValidUser = response.message;
            }
            toggleCheckOrderListAndSummary(order_no, order_process);
        },
        error: function () {
            console.log("error");
        }
    });
}

function callValidateCheckingOrderNo(order_no) {
    $("#unshippable-order-no").val(order_no)

    $.ajax({
        url: '/operation/checkOrderNo?order_no=' + order_no,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            var code = response.code;
            var message = response.message;
            console.log(response)
            if(code == "200") {
                console.log("success")
                $('#unshippable').attr("action","/operation/order-unshippable");
                $('#unshippable').attr("method","POST");
                $('#unshippable').submit();    
            } else{
                alert(message + " 상태인 주문건이여서 미출고 처리할 수 없습니다.");
            }
        },
        error: function () {
            console.log("error");
        }
    })
}

function validatePackingRequest(order_no) {
    $.ajax({
        url: '/operation/validatePackingRequest?order_no=' + order_no,
        type: 'GET',
        success: function (response) {
            isValidRequest = response.object;

            if (isValidRequest) {
                getOrderPackingDetails(order_no);
                saveOrderPacking(order_no);
            } else {
                getOrderPackingDetails(order_no);
                getShippingTrackingNoList(order_no);
            }
        }
    });
}