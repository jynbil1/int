var redirect_;
var summary_details;
var isValidUser;

$(document).ready(function () {

    $('#orderNoSubmit').on("click", function () {
        var i_order_no = $("#fetchLandingPage").val();
        var i_order_process = "order_inspection";
        callValidateCheckingUser(i_order_no, i_order_process);
    }).hide();

    $('#fetchLandingPage').keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            $('#orderNoSubmit').click();
        }
    });

    $("html").click(function () {
        $("#productNoInput").focus();
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
                $("div[id='div-main-inspection-page']").hide();
                $("div[id='pageDescription_1']").hide();
            } else if (code == '200') {
                $("div[id='div-main-inspection-page']").hide();
                $("div[id='pageDescription_1']").hide();
                if (isValidUser === true) {
                    getOrderInspectionList(order_no);
                    $("#div-order-check-table").css("display", "block");
                } else {
                    if (confirm(isValidUser + ' is currently working on this inspection, which means you cannot make changes, unless you take over.')) {
                        getOrderInspectionList(order_no);
                        $("#div-order-check-table").css("display", "block");
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

function getOrderInspectionList(order_no) {
    $.ajax({
        url: '/operation/getOrderInspectionList?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            // console.log(" ============ response  :   " + JSON.stringify(jsonObject));

            var pageDesc = '<p> 상품의 바코드를 스캔해서 올바른 상품이 피킹이 되었는지 확인해 주세요.</p>';
            $('div#pageDescription_2').html(pageDesc);

            var dateFormat = summary_details.order_date;
            var getDate = dateFormat.split("T");
            var date = getDate[0];
            var getTime = getDate[1].split(".");
            var time = getTime[0];

            var tableDetails = '<tr> <td class="td-order-no"> #' + order_no + '</td><td class="td-order-date" colspan="3"><span>주문 일자: <span class="date-format">' + date + ' ' + time + '</span></span></td>' +
                '<tr><td class="td-order-cname"> 고객명: ' + summary_details.customer_name + ' </td>' +
                '<td class="td-order-cemail"> E-mail: ' + summary_details.customer_email + ' </td>' +
                '<td class="td-order-total"> 주문액: $' + summary_details.total + ' </td>' +
                '<td class="td-order-btn"><button type="submit" class="unshippableOrder button primary" data-order-no="' + order_no + '">미출고</button>' +
                '<button type="submit" id="finishScanning" class="button primary" onclick="finishScanning(' + order_no + ');">검수 완료처리</button>' +
                '<button type="submit" id="cancelInspection" class="button greyed">검수 취소 </button>' + 
                '</td></tr>'

            $('tbody#tableDetails').html(tableDetails);

            var productNoInputDiv = '<p class="warning-msg"></p><input type="text" id="productNoInput" name="i_product_no" autofocus placeholder="상품 검수 | 상품 바코드를 입력하세요."><span class="material-icons-outlined"> document_scanner </span> <input type="text" id="productNoInputCancel" name="i_product_no_cancel" autofocus placeholder="상품 검수 | 검수 취소할 상품 바코드를 입력하세요."><span class="material-icons-outlined"> document_scanner </span>';
            productNoInputDiv += ' <button type="submit" id="submitProductNo"> </button><button type="submit" id="submitProductNoCancel"> </button>';
            $('div#productNoInputScannerDiv').html(productNoInputDiv);

            var tableHeaders = '';
            tableHeaders += '<tr>' +
                ' <th> 상품 ID </th>' +
                ' <th> 상품명 </th>' +
                ' <th> 주문 수량 </th>' +
                //' <th> 검수 수량 </th>' +
                ' <th> 바코드 </th>' +
                ' <th> </th>' +
                '</tr>;'
            $('thead#orderInspectionListHeader').html(tableHeaders);

            var tableRows = '';
            $.each(jsonObject, function (i, object) {
                //processed_count === null -> no change
                // if 0 or less that qty -> red
                // if processed_count === qty -> green
                var p_count = object.processed_count === null ? 0 : object.processed_count;
                var barcode = object.barcode === null ? "" : object.barcode;
                tableRows += '<tr class="row ' + object.product_no + '">' +
                    '<td class="product-no">' + object.product_no + '</td>' +
                    '<td class="product-name">' + object.product_name + '</td>' +
                    '<td class="quantity">' + object.quantity + '</td>' +
                    //'<td class="processed-count">' + p_count + '</td>' +
                    '<td class="barcode" data-barcode="' + barcode + '">' + barcode + '</td>' +
                    //'<td class="manual-inspection"> <button id="manualCompleteCountBtn" class="manualCompleteCount" data-product="' +
                    //object.product_no + '" > 수동 검수 </button>' +
                    //'<button  id="manualClearCountBtn" class="manualClearCount" data-productclear="' + object.product_no + '"> 검수취소 </button></td>' +
                    '</tr>;'
            });

            $('tbody#orderInspectionList').html(tableRows);
            $('#productNoInput').focus();
            $('#submitProductNo').on("click", function () {
                var i_product_barcode = $("#productNoInput").val();
                scanAndIncrementCount(i_product_barcode, order_no);
            });
            $('#submitProductNo').hide();
            $('#submitProductNoCancel').on("click", function () {
                var i_product_barcode = $("#productNoInputCancel").val();
                scanAndDecreaseCount(i_product_barcode, order_no);
                toggleCancelScanBtn();
            });
            $('#submitProductNoCancel').hide();
            $('.manualClearCount').hide();

            $("#productNoInputCancel").hide();
            $(".cancelScannedProductActive").hide();

            $('.cancelScannedProduct').on("click", function () {
                toggleCancelScan();
                $(this).hide();
                $(".cancelScannedProductActive").show();
            });

            $('.cancelScannedProductActive').on("click", function () {
                toggleIncrementScan();
                $(this).hide();
                $(".cancelScannedProduct").show();
            });


            //$('.row').each(function () {
            //    var input_qty = parseInt($(this).find(".quantity").text().trim());
            //    var input_pc = parseInt($(this).find(".processed-count").text().trim());

            //    if (input_qty != input_pc && input_pc != 0) {
            //        $(this).addClass("row-red");
            //        $(this).removeClass("row-green");
            //    }
            //    if (input_qty == input_pc) {
            //        $(this).addClass("row-green");
            //        $(this).removeClass("row-red");
            //        $(this).find('#manualClearCountBtn').show();
            //        $(this).find('#manualCompleteCountBtn').hide();
            //    }
            //});


            $('#productNoInput').keypress(function (e) {
                var key = e.which;
                if (key == 13) {
                    $('#submitProductNo').click();
                    $("#productNoInput").val("");
                }
            });

            $('#productNoInputCancel').keypress(function (e) {
                var key = e.which;
                if (key == 13) {
                    $('#submitProductNoCancel').click();
                    toggleIncrementScan();
                }
            });

            $('.manualCompleteCount').on("click", function () {
                var product_no = $(this).data("product");
                if (confirm('Are you sure you want to complete scanning this Product?')) {
                    var qty = $('tr.' + product_no + '> td.quantity').text();
                    $('tr.' + product_no + '> td.processed-count').text(qty);
                    $(this).hide();
                    $(this).siblings().show();

                    parentRow = $(this).parent().parent();
                    var input_qty = parseInt(parentRow.find(".quantity").text().trim());
                    var input_pc = parseInt(parentRow.find(".processed-count").text().trim());
                    if (input_pc > input_qty) {
                        parentRow.addClass("row-red");
                        parentRow.removeClass("row-green");
                        parentRow.prependTo("#orderInspectionList");
                    }
                    if (input_pc === input_qty) {
                        parentRow.addClass("row-green");
                        parentRow.removeClass("row-red");
                        parentRow.appendTo("#orderInspectionList");
                    }
                    if (input_pc < input_qty) {
                        parentRow.removeClass("row-green");
                        parentRow.removeClass("row-red");
                    }
                } else {
                    console.log('No changes has been made.');
                }
            });

            $('.manualClearCount').on("click", function () {
                var p_no = $(this).data("productclear");
                if (confirm('Are you sure you want to clear scanned Product?')) {
                    $('tr.' + p_no + '> td.processed-count').text(0);
                    $(this).hide();
                    $(this).siblings().show();

                    parentRow = $(this).parent().parent();
                    var input_qty = parseInt(parentRow.find(".quantity").text().trim());
                    var input_pc = parseInt(parentRow.find(".processed-count").text().trim());
                    if (input_pc > input_qty) {
                        parentRow.addClass("row-red");
                        parentRow.removeClass("row-green");
                        parentRow.prependTo("#orderInspectionList");
                    }
                    if (input_pc === input_qty) {
                        parentRow.addClass("row-green");
                        parentRow.removeClass("row-red");
                        parentRow.appendTo("#orderInspectionList");
                    }
                    if (input_pc < input_qty) {
                        parentRow.removeClass("row-green");
                        parentRow.removeClass("row-red");
                    }
                } else {
                    console.log('No changes has been made.');
                }

            });

            $('.saveScannedProducts').on("click", function () {
                saveAllScanned($(this).data("order-no"));
                $(".loading").css("display", "table");
            });

            $('#cancelInspection').on("click", function () {
                if (confirm('Are you sure you want to discard all scanned product/s ?')) {
                    redirectToOperation();
                } else {
                    console.log('No changes has been made.');
                }
            });

            $('.unshippableOrder').on("click", function () {
                callValidateCheckingOrderNo($(this).data("order-no"));
            });
        },
        error: function () {
            console.log("error");
        }
    });
}

function scanAndIncrementCount(i_product_barcode, order_no) {
    var row = $(".row").length;
    var row_green = $(".row-green").length;

    if (i_product_barcode === order_no && row_green > 0) {
        saveAllScanned(order_no);
    } else if (i_product_barcode === order_no && row != row_green) {
        $(".warning-msg").html("<span>⚠</span> Please inspect at least 1 product.");
        $(".warning-msg").fadeIn(500, function () {
            $(this).fadeOut(4000);
        });
    } else {
        if ($("[data-barcode='" + i_product_barcode + "']").length) {
            $("[data-barcode='" + i_product_barcode + "']").parent().addClass("row-green");
            $("[data-barcode='" + i_product_barcode + "']").parent().appendTo("#orderInspectionList");
        } else {
            $(".warning-msg").html("<span>⚠</span> Barcode is invalid.");
            $(".warning-msg").fadeIn(500, function () {
                $(this).fadeOut(4000);
            });
        }
    }
}

function scanAndDecreaseCount(i_product_barcode) {
    $('#orderInspectionList tr').each(function () {
        var self = $(this);
        var product_barcode = self.find("td:eq(4)").text().trim();
        if (product_barcode === i_product_barcode && product_barcode !== '') {
            var processed_count = self.find("td:eq(3)").text().trim();
            if (processed_count === 'null' || processed_count === '') {
                self.find("td:eq(3)").text(1)
            } else {
                self.find("td:eq(3)").text(parseInt(processed_count) - parseInt(1))
            }
        }
        var input_qty = parseInt($(this).find(".quantity").text().trim());
        var input_pc = parseInt($(this).find(".processed-count").text().trim());
        if (input_pc > input_qty) {
            $(this).addClass("row-red");
            $(this).removeClass("row-green");
            //$('.checkbox-table').find('tr:first').after(this);
        } else if (input_pc === input_qty) {
            $(this).addClass("row-green");
            $(this).removeClass("row-red");
            $('.checkbox-table').find('tr:last').after(this);
        } else if (input_pc < input_qty) {
            $(this).removeClass("row-green");
            $(this).removeClass("row-red");
        }

    });
}

function toggleCancelScan() {
    $("#productNoInputCancel").show();
    $("#productNoInputCancel").focus();
    $("#productNoInputCancel").val("");
    $("#productNoInput").hide();
}

function toggleIncrementScan() {
    $("#productNoInput").show();
    $("#productNoInput").focus();
    $("#productNoInput").val("");
    $("#productNoInputCancel").hide();
}

function toggleCancelScanBtn() {
    $("#productNoInput").show();
    $("#productNoInput").focus();
    $("#productNoInput").val("");
    $("#productNoInputCancel").hide();
    $(".cancelScannedProductActive").hide();
    $(".cancelScannedProduct").show();
}

function saveAllScanned(order_no) {
    var payload = {
        'order_no': order_no,
        'request_list': []
    };

    $('#orderInspectionList tr').each(function () {
        var self = $(this);
        var product_id = self.find("td:eq(0)").text().trim();
        var qty = self.find("td:eq(2)").text().trim();
        var processed_count = 0;
        var req_detail = {
            'order_no': order_no,
            'product_no': product_id,
            'processed_count': parseInt(processed_count),
            'quantity': parseInt(qty)
        };
        payload.request_list.push(req_detail);
    });
    callSaveCheckSummaryAndDetails(payload);
}

function callSaveCheckSummaryAndDetails(payload) {
    $('.loading').css('display', 'table');
    $.ajax({
        url: '/operation/insertCheckedOrders',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(payload),
        dataType: "json",
        success: function (response) {
            console.log("---------------call save order check details-----------");
            console.log(response);
            if (response.code === '400') {
                alert(response.message);
            } else {
                redirectToOperation();
            }
            $('.loading').css('display', 'none');
        },
        error: function () {
            console.log("error");
        }

    });

}

function callGetSummaryDetails(order_no, order_process) {
    $.ajax({
        url: '/operation/getOrderCheckedSummary?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            console.log('line-437');
            console.log(JSON.stringify(response));
            if (response.code == "200") {
                console.log(response.message);
                summary_details = response.object;
                toggleCheckOrderListAndSummary(order_no, order_process);
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else {
                console.log(response.message);
            }
        },
        error: function () {
            console.log("error");
        }
    });
}

function callValidateCheckingUser(order_no, order_process) {
    $.ajax({
        url: '/operation/isCheckingUserValid?order_no=' + order_no,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            console.log(JSON.stringify(response));
            if (response.code == '200' || response.code == '400') {
                if (response.object === true) {
                    isValidUser = response.object;
                } else {
                    isValidUser = response.message;
                }
                callGetSummaryDetails(order_no, order_process);
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            }
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
            if (code == "200") {
                console.log("success")
                $('#unshippable').attr("action", "/operation/order-unshippable");
                $('#unshippable').attr("method", "POST");
                $('#unshippable').submit();
            } else {
                alert(message + " 상태인 주문건이여서 미출고 처리할 수 없습니다.");
            }
        },
        error: function () {
            console.log("error");
        }
    });
}

function finishScanning(order_no) {
    if (confirm("정상적인 검수 완료 처리가 아닙니다. 강제 완료 처리 하시겠습니까 ?")) {
        saveAllScanned(order_no);
    }
}