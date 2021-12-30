var sortBy;
var orderBy;
var currentPage;
var itemRange;
var searchKeyword;
var transactionRow;
var totalPage;
var totalCount;
var orderStatus;
var taskID;

$(document).ready(function () {

    currentPage = $("#listPage").val();
    itemRange = $("#itemRange").val();
    searchKeyword = encodeURIComponent($(".search > input").val().trim());
    sortBy = $("#sortBy").val();
    orderBy = $("#orderBy").val();
    orderStatus = 'wc-processing';

    getOrderDetailCount(orderStatus);
    getOperationOrderPackingListCount(searchKeyword, orderStatus);

    $("#orderNo, #customerName, #orderDate, #orderTrackingNo, #prodID, #prodName, #shipmentDate").on("click", function () {
        var order = $(this).attr("data-order");
        var sort = $(this).attr("data-sort");

        if (order == 1) {
            $(this).attr("data-order", 0);

            if ($('#special_orders').is(":checked")) {
                getOperationSpecialOrderList(currentPage, itemRange, searchKeyword, sort, 0, orderStatus);
            } else if ($('#moomoo_together').is(":checked")) {
                getMoomooTogetherList(currentPage, itemRange, searchKeyword, sort, 0);
            } else {
                getOperationOrderPackingList(currentPage, itemRange, searchKeyword, sort, 0, orderStatus);
            }

            $("#orderBy").val(order);
            $("#sortBy").val(sort);
            sortBy = sort;
            orderBy = order;
        } else {
            $(this).attr("data-order", 1);

            if ($('#special_orders').is(":checked")) {
                getOperationSpecialOrderList(currentPage, itemRange, searchKeyword, sort, 1, orderStatus);
            } else if ($('#moomoo_together').is(":checked")) {
                getMoomooTogetherList(currentPage, itemRange, searchKeyword, sort, 1);
            } else {
                getOperationOrderPackingList(currentPage, itemRange, searchKeyword, sort, 1, orderStatus);
            }
            $("#orderBy").val(order);
            $("#sortBy").val(sort);
            sortBy = sort;
            orderBy = order;
        }
    });

    $("#searchBtn").on("click", function (event) {
        event.preventDefault();
        searchKeyword = encodeURIComponent($(".search > input").val().trim())
        currentPage = 1;
        $("#listPage").val(currentPage);
        $("#searchKeyword").val(decodeURIComponent(searchKeyword));
        if ($('#special_orders').is(":checked")) {
            getOperationSpecialOrderListCount(searchKeyword, orderStatus);
        } else if ($('#moomoo_together').is(":checked")) {
            getMoomooTogetherListCount(searchKeyword);
        } else {
            getOperationOrderPackingListCount(searchKeyword, orderStatus);
        }
    });

    $(".search > input").keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            currentPage = 1;
            $("#listPage").val(currentPage);
            searchKeyword = encodeURIComponent($(this).val().trim());
            if ($('#special_orders').is(":checked")) {
                getOperationSpecialOrderListCount(searchKeyword, orderStatus);
            } else if ($('#moomoo_together').is(":checked")) {
                getMoomooTogetherListCount(searchKeyword);
            } else {
                getOperationOrderPackingListCount(searchKeyword, orderStatus);
            }
            $("#searchKeyword").val(decodeURIComponent(searchKeyword));
        }
    });

    $('#item_range').change(function () {
        currentPage = 1;
        $("#listPage").val(currentPage);
        orderBy = $("#orderBy").val();

        if ($(this).val() == "all") {
            itemRange = $("#totalProducts").text();
        } else {
            itemRange = $(this).val();
        }

        $("#itemRange").val(itemRange);
        if ($('#special_orders').is(":checked")) {
            getOperationSpecialOrderListCount(searchKeyword, orderStatus);
        } else if ($('#moomoo_together').is(":checked")) {
            getMoomooTogetherListCount(searchKeyword);
        } else {
            getOperationOrderPackingListCount(searchKeyword, orderStatus);
        }
    });

    $("#generateSelected").on("click", function () {
        var payload = {
            "order_details": []
        };

        if ($("input[name='order-item']:checked").length > 0) {
            $.each($("input[name='order-item']:checked"), function (index) {
                var item_in_row = $(this).attr('data-value');
                item_in_row = JSON.parse(item_in_row);
                payload.order_details.push(item_in_row);
            });
            generateMultipleItemsPDF(payload);
        } else {
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>Please select an order to process.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
        }
    });

    $("#generateAll").on("click", function () {
        callGenerateAllProcessingOrderPDF();
    });

    $('#datePicker').on("change", function () {
        $(".search > input").val($(this).val());
    });

    $("#generateDHL").on("click", function () {
        progressBar("");
    });
});

function getOperationOrderPackingList(page, item_range, keyword, sort, order, order_status) {
    $.ajax({
        url: '/getOperationOrderPackingList?page=' + page + '&item_range=' + item_range + '&keyword=' + keyword + '&sort=' + sort + '&order=' + order + '&order_status=' + order_status,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            if (response.code == 200) {
                if (jsonObject.length > 0) {
                    $.each(jsonObject, function (i, object) {
                        var tracking_no = object.tracking_no ? object.tracking_no : "";
                        var d_order_status = convertOrderStatus(object.order_status)
                        var item_detail_json = {
                            'order_no': object.order_no,
                            'order_date': object.order_date,
                            'order_status': object.order_status
                        };
                        var item_detail_str = JSON.stringify(item_detail_json);
                        tr += '<tr data-order-no="' + object.order_no + '">' +
                            "<td class='item-check-box'> <input type='checkbox' name='order-item' data-value='" + item_detail_str + "' /></td>" +
                            '<td class="order-no">' + object.order_no + '</td>' +
                            '<td class="customer-name">' + object.customer_name + '</td>' +
                            '<td class="order-date">' + object.order_date + '</td>' +
                            '<td class="order-tracking-no">' + tracking_no + '</td>' +
                            '<td class="customer-email">' + object.customer_email + '</td>' +
                            '<td class="order-subtotal">' + object.subtotal + '</td>' +
                            '<td class="order-total">' + object.total + '</td>' +
                            '<td class="generate-btn"><a class="generate-pdf" onclick="generateSingleItemPDF(' + object.order_no + ",'" + object.order_date + "','" + object.order_status + "'" + ')">주문서</a></td>';
                    });
                } else {
                    tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">No records found.<span></span></span></td></tr>';
                }
            } else {
                tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">No records found.<span></span></span></td></tr>';
            }
            $('.loading').css('display', 'none');
            $('tbody#operationOrderPackingList').html(tr);
        },
        error: function () {
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>An error has occurred, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
        }
    });
}

function convertOrderStatus(db_order_status) {
    if (db_order_status === "wc-processing") {
        return "처리대기";
    } else if (db_order_status === "wc-preparing") {
        return "운영중";
    } else if (db_order_status === "wc-shipped") {
        return "배송중";
    } else if (db_order_status === "wc-completed") {
        return "배송 완료";
    } else if (db_order_status === "wc-refunded") {
        return "환불 완료";
    } else if (db_order_status === "wc-cancelled") {
        return "취소 완료";
    } else {
        return "외";
    }
}

function generateMultipleItemsPDF(pdf_payload) {
    var timeout = 3500 + (1000 * pdf_payload.order_details.length);
    callPdfGeneratorAPI(pdf_payload, timeout);
}

function generateSingleItemPDF(order_no, order_date, order_status) {
    var pdf_payload = {
        "order_details": [{
            "order_no": order_no,
            "order_date": order_date,
            "order_status": order_status
        }]
    }
    callPdfGeneratorAPI(pdf_payload, 5000);
}

function callPdfGeneratorAPI(pdf_payload, timeout) {
    $('.loading').css('display', 'table');
    $.ajax({
        type: 'POST',
        contentType: "application/json",
        url: '/generateOperationOrderPackingSlip',
        data: JSON.stringify(pdf_payload),
        responseType: 'blob',
        success: function (response) {
            openPDFOnNewTab(response);
            $('.loading').css('display', 'none');
        },
        error: function () {
            $('.loading').css('display', 'none');
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>An error has occurred, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
        }
    });
}

function callGenerateAllProcessingOrderPDF() {
    $('.loading').css('display', 'table');
    var count = parseInt($("#totalProducts").text());
    if (count > 0) {
        $.ajax({
            type: 'GET',
            contentType: "application/json",
            url: '/generateAllOperationOrderPackingSlip?order_status=wc-processing',
            responseType: 'blob',
            success: function (response) {
                openPDFOnNewTab(response);
                $('.loading').css('display', 'none');
            }
        });
    } else {
        alert('No available Processing Order to be Generated!');
        $('.loading').css('display', 'none');
    }
}

function openPDFOnNewTab(base64EncodedPDF) {
    var byteCharacters = atob(base64EncodedPDF);
    var byteNumbers = new Array(byteCharacters.length);
    for (var i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    var byteArray = new Uint8Array(byteNumbers);
    var file = new Blob([byteArray], { type: 'application/pdf;base64' });
    var fileURL = URL.createObjectURL(file);
    window.open(fileURL);
    getOrderDetailCount(orderStatus);

    
}

function checkAll(bx) {
    var cbs = document.getElementsByTagName('input');
    for (var i = 0; i < cbs.length; i++) {
        if (cbs[i].type == 'checkbox') {
            cbs[i].checked = bx.checked;
        }
    }
}

function getOrderDetailCount(order_status) {
    $.ajax({
        url: '/getOrderDetailCount',
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            var moomoo = jsonObject.moomoo ? jsonObject.moomoo : 0;
            var moomootogether = jsonObject.moomootogether ? jsonObject.moomootogether : 0;
            var usdomestic = jsonObject.usdomestic ? jsonObject.usdomestic : 0;
            var usdomestic2 = jsonObject.usdomestic2 ? jsonObject.usdomestic2 : 0;

            tr += '<tr>' +
                '<td class="total" data-val="wc-total"><span>전체 (' + jsonObject.total + ')</span></td>' +
                '<td class="processing" data-val="wc-processing" data-count="' + jsonObject.processing + '"><span>처리대기 (' + jsonObject.processing + ')</span></td>' +
                '<td class="preparing" data-val="wc-preparing" data-count="' + jsonObject.preparing + '"><span>운영중 (' + jsonObject.preparing + ')</span></td>' +
                '<td class="shipped" data-val="wc-shipped" data-count="' + jsonObject.shipped + '"><span>배송중 (' + jsonObject.shipped + ')</span></td>' +
                '<td class="completed" data-val="wc-completed" data-count="' + jsonObject.completed + '"><span>배송완료 (' + jsonObject.completed + ')</td>' +
                '<td class="refunded" data-val="wc-refunded" data-count="' + jsonObject.refunded + '"><span>환불됨 (' + jsonObject.refunded + ')</span></td>' +
                '<td class="cancelled" data-val="wc-cancelled" data-count="' + jsonObject.cancelled + '"><span>취소됨 (' + jsonObject.cancelled + ')</span></td>' +
                '<td class="moomoo" data-val="wc-moomoo" data-count="' + moomoo + '"><span>반짝 (' + moomoo + ')</span></td>' +
                '<td class="moomootogether" data-val="wc-moomootogether" data-count="' + moomootogether + '"><span>합배송 (' + moomootogether + ')</span></td>' +
                '<td class="usdomestic" data-val="wc-usdomestic" data-count="' + usdomestic + '"><span>미국내 (' + usdomestic + ')</span></td>' +
                '<td class="usdomestic2" data-val="wc-usdomestic2" data-count="' + usdomestic2 + '"><span>미국내2 (' + usdomestic2 + ')</span></td>';
            $('tbody#orderCountSummary').html(tr);
            $("tbody#orderCountSummary tr td[data-val='" + order_status + "']").children('span').addClass("active");

            if($("tbody#orderCountSummary tr td span.active").parent().data("count") == 0) {
                $("#generateSelected, #generateAll").prop("disabled", true).removeClass("primary").addClass("disabled");
            } else {
                $("#generateSelected, #generateAll").prop("disabled", false).removeClass("disabled").addClass("primary");
            }

            $("td.total, td.processing, td.preparing, td.shipped, td.completed, td.refunded, td.cancelled").on("click", function () {
                $(this).siblings().find("span").removeClass("active");
                $(this).find("span").addClass("active");
                $("#generateDHL").prop("disabled", true).removeClass("primary").addClass("disabled");
                $('#special_orders').prop('checked', false);
                $('#moomoo_together').prop('checked', false);
                $('.loading').css('display', 'table');
                $("#prodID, #prodName, #shipmentDate").parent().hide();
                $("#orderTrackingNo").parent().show();
                orderStatus = $(this).attr("data-val");
                currentPage = 1;
                searchKeyword = "";
                $("#searchKeyword").val(searchKeyword);
                $(".search > input").val(searchKeyword);
                
                getOperationOrderPackingListCount(searchKeyword, orderStatus);

                if ($(this).attr("data-count") == 0) {
                    $("#generateSelected").prop("disabled", true).removeClass("primary").addClass("disabled");
                } else {
                    $("#generateSelected").prop("disabled", false).removeClass("disabled").addClass("primary");
                }

                if ($(this).attr("data-val") != "wc-processing") {
                    $("#generateAll").prop("disabled", true).removeClass("primary").addClass("disabled");
                } else {
                    if ($(this).attr("data-count") == 0) {
                        $("#generateAll").prop("disabled", true).removeClass("primary").addClass("disabled");
                    } else {
                        $("#generateAll").prop("disabled", false).removeClass("disabled").addClass("primary");
                    }
                }
            });

            $("td.moomoo, td.usdomestic, td.usdomestic2").on("click", function () {
                $("#generateAll").prop("disabled", true).removeClass("primary").addClass("disabled");
                $('#special_orders').prop('checked', true);
                $('#moomoo_together').prop('checked', false);
                $(this).siblings().find("span").removeClass("active");
                $(this).find("span").addClass("active");
                $('.loading').css('display', 'table');
                $("#prodID, #prodName, #orderTrackingNo").parent().show();
                $("#shipmentDate").parent().hide();
                orderStatus = $(this).attr("data-val");
                currentPage = 1;
                searchKeyword = "";
                $("#searchKeyword").val(searchKeyword);
                $(".search > input").val(searchKeyword);
                
                getOperationSpecialOrderListCount(searchKeyword, orderStatus);
                if ($(this).attr("data-count") == 0) {
                    $("#generateDHL, #generateSelected").prop("disabled", true).removeClass("primary").addClass("disabled");
                } else {
                    $("#generateDHL, #generateSelected").prop("disabled", false).removeClass("disabled").addClass("primary");
                }
            });

            $("td.moomootogether").on("click", function () {
                $("#generateAll").prop("disabled", true).removeClass("primary").addClass("disabled");
                $('#moomoo_together').prop('checked', true);
                $('#special_orders').prop('checked', false);
                $(this).siblings().find("span").removeClass("active");
                $(this).find("span").addClass("active");
                $('.loading').css('display', 'table');
                $("#prodID, #prodName").parent().hide();
                $("#orderTrackingNo").parent().hide();
                $("#shipmentDate").parent().show();
                orderStatus = $(this).attr("data-val");
                currentPage = 1;
                searchKeyword = "";
                $("#searchKeyword").val(searchKeyword);
                $(".search > input").val(searchKeyword);

                getMoomooTogetherListCount(searchKeyword);

                if ($(this).attr("data-count") == 0) {
                    $("#generateDHL, #generateSelected").prop("disabled", true).removeClass("primary").addClass("disabled");
                } else {
                    $("#generateDHL, #generateSelected").prop("disabled", false).removeClass("disabled").addClass("primary");
                }
            });
        },
        error: function () {
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>An error has occurred, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
        }
    });
}

function createPagination(current, total) {
    var delta = 1,
        left = parseInt(current) - delta,
        right = parseInt(current) + delta + 1,
        range = [],
        rangeWithDots = [],
        l;

    for (let i = 1; i <= parseInt(total); i++) {
        if (i == 1 || i == parseInt(total) || i >= left && i < right) {
            range.push(i);
        }
    }

    for (let i of range) {
        if (l) {
            if (i - l === 2) {
                rangeWithDots.push(l + 1);
            } else if (i - l !== 1) {
                rangeWithDots.push('...');
            }
        }
        rangeWithDots.push(i);
        l = i;
    }
    if ($("#item_range").val() == "all") {
        itemRange = $("#totalProducts").text();
    } else {
        itemRange = $("#item_range").val();
    }
    $("#itemRange").val(itemRange);
    if ($('#special_orders').is(":checked")) {
        getOperationSpecialOrderList(current, itemRange, searchKeyword, sortBy, orderBy, orderStatus);
    } else if ($('#moomoo_together').is(":checked")) {
        getMoomooTogetherList(current, itemRange, searchKeyword, sortBy, orderBy);
    } else {
        getOperationOrderPackingList(current, itemRange, searchKeyword, sortBy, orderBy, orderStatus);
    }

    currentPage = current;
    var pageTable = "<ul class='page-list'>";
    pageTable += "<li onclick='createPagination(" + '"1","' + totalPage + '"' + ");'>&lt;&lt;</li>";

    for (var pageCell of rangeWithDots) {
        if (pageCell != '...') {
            pageTable += '<li onclick="createPagination(' + "'" + pageCell + "','" + totalPage + "'" + ');">' + pageCell + '</li>';
        } else {
            pageTable += '<li>' + pageCell + '</li>';
        }
    }

    pageTable += "</li><li onclick='createPagination(" + '"' + totalPage + '","' + totalPage + '"' + ");'>&gt;&gt;</li>";
    document.getElementById("pagination").innerHTML = pageTable;

    var aTags = document.getElementsByTagName("li");
    var found;

    for (var i = 0; i < aTags.length; i++) {
        if (aTags[i].textContent == parseInt(current)) {
            found = aTags[i];
            found.classList.add("active");
            break;
        }
    }
}

function getOperationOrderPackingListCount(keyword, order_status) {
    var tr = "";
    $.ajax({
        url: '/getOperationOrderPackingListCount?keyword=' + keyword + '&order_status=' + order_status,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            count = Number(jsonObject);
            $("#totalProducts").text(count);
            totalPage = Math.ceil(count / itemRange);
            createPagination(currentPage, totalPage);
        },
        async: true,
        error: function () {
            $(".loading").css("display", "none");
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>Error in loading list, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
            tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">Error in loading list, please contact Dev.<span></span></span></td></tr>';
            $('tbody#operationOrderPackingList').html(tr);
        }
    });
}

function getOperationSpecialOrderList(page, item_range, keyword, sort, order, order_status) {
    $.ajax({
        url: '/getOperationSpecialOrderList?page=' + page + '&item_range=' + item_range + '&keyword=' + keyword + '&sort=' + sort + '&order=' + order + '&order_status=' + order_status,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            var tr = '';
            const jsonObject = response.object;
            if (response.code == 200) {
                if (jsonObject.length > 0) {
                    $.each(jsonObject, function (i, object) {
                        var tracking_no = object.tracking_no ? object.tracking_no : "";
                        var d_order_status = convertOrderStatus(object.order_status)
                        var item_detail_json = {
                            'order_no': object.order_no,
                            'order_date': object.order_date,
                            'order_status': object.order_status
                        };
                        var item_detail_str = JSON.stringify(item_detail_json);
                        tr += '<tr data-order-no="' + object.order_no + '">' +
                            "<td class='item-check-box'> <input type='checkbox' name='order-item' data-value='" + item_detail_str + "' /></td>" +
                            '<td class="order-no">' + object.order_no + '</td>' +
                            '<td class="product-id">' + object.product_id + '</td>' +
                            '<td class="product-name">' + object.product_name + '</td>' +
                            '<td class="customer-name">' + object.customer_name + '</td>' +
                            '<td class="order-date">' + object.order_date + '</td>' +
                            '<td class="order-tracking-no">' + tracking_no + '</td>' +
                            '<td class="customer-email">' + object.customer_email + '</td>' +
                            '<td class="order-subtotal">' + object.subtotal + '</td>' +
                            '<td class="order-total">' + object.total + '</td>' +
                            '<td class="generate-btn"><a class="generate-dhl" onclick="progressBar(' + object.order_no + ')">DHL</a>' +
                            '<a class="generate-pdf" onclick="generateSingleItemPDF(' + object.order_no + ",'" + object.order_date + "','" + object.order_status + "'" + ')">주문서</a></td>';
                    });
                } else {
                    tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">No records found.<span></span></span></td></tr>';
                }
            } else {
                tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">No records found.<span></span></span></td></tr>';
            }
            $('.loading').css('display', 'none');
            $('tbody#operationOrderPackingList').html(tr);
        },
        error: function () {
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>An error has occurred, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
        }
    });
}

function getOperationSpecialOrderListCount(keyword, order_status) {
    var tr = "";
    $.ajax({
        url: '/getOperationSpecialOrderListCount?keyword=' + keyword + '&order_status=' + order_status,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            count = Number(jsonObject);
            $("#totalProducts").text(count);
            totalPage = Math.ceil(count / itemRange);
            createPagination(currentPage, totalPage);
        },
        async: true,
        error: function () {
            $(".loading").css("display", "none");
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>Error in loading list, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
            tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">Error in loading list, please contact Dev.<span></span></span></td></tr>';
            $('tbody#operationOrderPackingList').html(tr);
        }
    });
}

function getMoomooTogetherList(page, item_range, keyword, sort, order) {
    $.ajax({
        url: '/getMoomooTogetherList?page=' + page + '&item_range=' + item_range + '&keyword=' + keyword + '&sort=' + sort + '&order=' + order,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            var tr = '';
            const jsonObject = response.object;
            if (response.code == 200) {
                if (jsonObject.length > 0) {
                    $.each(jsonObject, function (i, object) {
                        var d_order_status = convertOrderStatus(object.order_status)
                        var item_detail_json = {
                            'order_no': object.order_no,
                            'order_date': object.order_date,
                            'order_status': object.order_status
                        };
                        var item_detail_str = JSON.stringify(item_detail_json);
                        tr += '<tr data-order-no="' + object.order_no + '">' +
                            "<td class='item-check-box'> <input type='checkbox' name='order-item' data-value='" + item_detail_str + "' /></td>" +
                            '<td class="order-no">' + object.order_no + '</td>' +
                            '<td class="customer-name">' + object.customer_name + '</td>' +
                            '<td class="order-date">' + object.order_date + '</td>' +
                            '<td class="ship-date">' + object.ship_date + '</td>' +
                            '<td class="customer-email">' + object.customer_email + '</td>' +
                            '<td class="order-subtotal">' + object.subtotal + '</td>' +
                            '<td class="order-total">' + object.total + '</td>' +
                            '<td class="generate-btn"><a class="generate-dhl" onclick="progressBar(' + object.order_no + ')">DHL</a>' +
                            '<a class="generate-pdf" onclick="generateSingleItemPDF(' + object.order_no + ",'" + object.order_date + "','" + object.order_status + "'" + ')">주문서</a></td>';
                    });
                } else {
                    tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">No records found.<span></span></span></td></tr>';
                }
            } else {
                tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">No records found.<span></span></span></td></tr>';
            }
            $('.loading').css('display', 'none');
            $('tbody#operationOrderPackingList').html(tr);
        },
        error: function () {
            $(".loading").css("display", "none");
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>Error in loading list, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
        }
    });
}

function getMoomooTogetherListCount(keyword) {
    var tr = "";
    $.ajax({
        url: '/getMoomooTogetherListCount?keyword=' + keyword,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            count = Number(jsonObject);
            $("#totalProducts").text(count);
            totalPage = Math.ceil(count / itemRange);
            createPagination(currentPage, totalPage);
        },
        async: true,
        error: function () {
            $(".loading").css("display", "none");
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>Error in loading list, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
            tr = '<tr><td colspan="12" style="text-align: center;"><span style="color: red; font-weight: 600;">Error in loading list, please contact Dev.<span></span></span></td></tr>';
            $('tbody#operationOrderPackingList').html(tr);
        }
    });
}

function progressBar(in_order_no) {
    var order_list = { "order_nos": [], "order_status" : "" };
    var checked_count = 0;
    var counter = 0;
    var interval = null;
    var status = "in_progress";

    if (in_order_no != "") {
        order_list.order_nos.push(in_order_no);
        order_list["order_status"] = orderStatus;
    } else {
        order_list["order_status"] = orderStatus;
        if ($("input[name='order-item']:checked").length > 0) {
            $.each($("input[name='order-item']:checked"), function () {
                var order_no = $(this).data('value').order_no;
                order_list.order_nos.push(order_no);
            });
        } else {
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>Please select an order to process.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
        }
    }
    
    checked_count = order_list.order_nos.length;

    if (checked_count > 0) {
        startSpecialOrderTask(order_list);
        $(".progress-container").css("display", "table");
        $(".progress-count").text("");
        $(".progress-bar-inner").css("width", "0%");

        var addInterval = function () {
            if (status == "in_progress") {
                $.ajax({
                    url: '/getSpecialOrderTaskProgress?task_id=' + taskID,
                    type: 'GET',
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function (response) {
                        const object = response.object;
                        $(".progress-bar-inner").each(function () {
                            counter = object.progress_count;
                            status = object.status;
                            let percent = counter;
                            percent = percent * 100 / checked_count;
                            if (percent > 100) {
                                percent = 100;
                            }
                            $(this).animate({ width: percent + '%' }, 500);
                        });
                        $(".progress-count").text(counter + "/" + checked_count);
                    },
                    async: true,
                    error: function () {
                        $(".warning-message").html("<p class='warning-msg'><span>⚠</span>An error has occurred, please contact Dev.</p>");
                        $(".warning-message").fadeIn(500, function () {
                            $(".warning-message").fadeOut(3000);
                        });
                    }
                });
            } else if (status == "with_error") {
                $(".warning-message").html("<p class='warning-msg'><span>⚠</span>An error has occurred, please contact Dev.</p>");
                $(".warning-message").fadeIn(500, function () {
                    $(".warning-message").fadeOut(3000);
                });
                $(".progress-container").css("display", "none");
                clearInterval(interval);
            } else {
                clearInterval(interval);
                getSpecialOrderBase64List(taskID);
            }
        };
        interval = setInterval(addInterval, 1000);
    }
}

function startSpecialOrderTask(order_list) {
    $.ajax({
        url: '/startSpecialOrderTask',
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(order_list),
        success: function (response) {
            taskID = response;
        },
        error: function () {
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>An error has occurred, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
        }
    });
}

function getSpecialOrderBase64List(task_id) {
    $.ajax({
        url: '/getSpecialOrderBase64List?task_id=' + task_id,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            printLabel(jsonObject);
        },
        async: true,
        error: function () {
            $(".warning-message").html("<p class='warning-msg'><span>⚠</span>An error has occurred, please contact Dev.</p>");
            $(".warning-message").fadeIn(500, function () {
                $(".warning-message").fadeOut(3000);
            });
        }
    });
}

function printLabel(fileURL) {
    var posturl;

    if (fileURL == undefined) {
        posturl = '/operation/printOrderPackingLabel'
    } else {
        posturl = '/operation/printOrderPackingLabel?shipping_label_url=' + fileURL
    }
    
    $.ajax({
        type: 'POST',
        url: posturl,
        responseType: 'blob',
        success: function (response) {
            $(".progress-container").css("display", "none");
            var byteCharacters = atob(response);
            var byteNumbers = new Array(byteCharacters.length);
            for (var i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            var byteArray = new Uint8Array(byteNumbers);
            var file = new Blob([byteArray], { type: 'application/pdf;base64' });
            var fileURL = URL.createObjectURL(file);
            var wnd = window.open(fileURL);
            wnd.print();
            //setTimeout(function () {
            //    wnd.close();
            //}, 4000);
        }
    })
}