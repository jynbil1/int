var sortBy;
var orderBy;
var currentPage;
var itemRange;
var searchKeyword;
var isUrgent = 0;
var orderDate;
var seeAll = 1;

var datePlaceHolder;
var clickedOrderSeq;

var orderOpen = 0;
var isToday = 0;
$(document).ready(function() {

    currentPage = $("#listPage").val();
    itemRange = $("#itemRange").val();
    searchKeyword = $(".search > input").val();
    sortBy = $("#sortBy").val();
    orderBy = $("#orderBy").val();

    getOrderList();
    getOrderListCount();
    isToday = 0;

    $("body").on("click", "#show-urgent-list", function() {
        isUrgent = $('#show-urgent-list').is(":checked") ? 1 : 0;
        getOrderList();
        getOrderListCount();
    });

    $("body").on("click", "#see-today", function() {
        seeAll = $('#see-today').is(":checked") ? 0 : 1;
        isToday = $('#see-today').is(":checked") ? 1 : 0;
        if (seeAll == 1) {
            orderDate = null;
        } else {
            orderDate = new Date().format('yyyy-MM-dd');
        }
        getOrderList();
        getOrderListCount();
    });

    $(".search").change(function() {
        orderDate = $('#order-date-filter').val();
        getOrderList();
        getOrderListCount();
    });

    $("#validDate, #trackingNo, #orderNo, #shippingMethod, #pic,\
        #validTime, #packStatus, #orderDate, #orderStatus, \
        #customerName, #email, #consignee, #address, \
        #city, #state, #postcode, #country, #total").on("click", function() {
        orderBy = $(this).attr("data-order");
        sortBy = $(this).attr("data-sort");

        if (orderBy == 1) {
            $(this).attr("data-order", 0);
            searchKeyword = $(".search > input").val();
            currentPage = 1;
            $("#listPage").val(currentPage);
            getTransactionListCount(searchKeyword, isUrgent);
            $("#orderBy").val(orderBy);
            $("#sortBy").val(sortBy);
        } else {
            $(this).attr("data-order", 1);
            searchKeyword = $(".search > input").val();
            currentPage = 1;
            $("#listPage").val(currentPage);
            getTransactionListCount(searchKeyword, isUrgent);
            $("#orderBy").val(orderBy);
            $("#sortBy").val(sortBy);
        }
    });

    $(".search > button").on("click", function(event) {
        event.preventDefault();
        searchKeyword = $(".search > input").val();
        currentPage = 1;
        $("#listPage").val(currentPage);
        getTransactionListCount(searchKeyword, isUrgent);
        $("#searchKeyword").val(searchKeyword);
    });

    $(".search > input").keypress(function(e) {
        var key = e.which;
        if (key == 13) {
            currentPage = 1;
            $("#listPage").val(currentPage);
            searchKeyword = $(this).val().trim();
            getTransactionListCount(searchKeyword, isUrgent);
            $("#searchKeyword").val(searchKeyword);
        }
    });

    $('#item_range').change(function() {
        currentPage = 1;
        $("#listPage").val(currentPage);
        itemRange = $(this).val();
        orderBy = $("#orderBy").val();
        console.log(itemRange);
        $("#itemRange").val(itemRange);
        getTransactionListCount(searchKeyword, isUrgent);
    });

    // $("body").on("click",
    //     ".order-date, .order-seq, .product-id, .comp-name,\
    //     .line_total, .hanpoom-pic",
    //     function() {
    //         if (orderOpen == 1) {
    //             orderOpen = 0;
    //             $(this).closest('tr').next('tr').hide();
    //         } else {
    //             $(this).closest('tr').next('tr').show();
    //             orderOpen = 1;
    //         }
    //     });

    // $("body").on("click", ".order-note-title", function() {
    //     orderOpen = 0;
    //     $(this).parent().hide();
    // });
});

function getTextLength(str) {
    var len = 0;
    for (var i = 0; i < str.length; i++) {
        if (escape(str.charAt(i)).length == 6) {
            len++;
        }
        len++;
    }
    return len;
}

function bytesHandler(obj) {
    var text = $(obj).val();
    $('.order-note-bytes').text(getTextLength(text) + '/100 자');
}

function reloadCurrentPage() {
    getOrderList();
    getOrderListCount();
    totalPage = Math.ceil(count / itemRange);
    createPagination(currentPage, totalPage);
}

function getOrderList() {
    $('.loading').css('display', 'table');
    $.ajax({
        url: '/shipment/getShippingLabelHistory?page=' + currentPage + '&item_range=' +
            itemRange + '&keyword=' + searchKeyword + '&sort=' + sortBy + '&order=' +
            orderBy + '&date=' + orderDate + '&is_urgent=' + isUrgent + '&see_all=' +
            seeAll + '&is_today=' + isToday,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                const jsonObject = response.object;
                var tr = '';
                if (jsonObject.length == 0) {
                    var tr = '<tr class="result-message">\
                            <td colspan="19">\
                                검색 결과가 없습니다.\
                            </td>\
                        </tr>';
                    $('tbody#historyList').html(tr);
                } else {
                    $.each(jsonObject, function(i, object) {
                        var formattedValidDate = object.valid_date == null ? "" : object.valid_date;
                        var formattedOrderDate = object.order_date == null ? "" : object.order_date;

                        var packStatusClass = object.process_status == "포장중" ? "in-packing" : object.process_status == "포장완료" ? "complete-packing" : "cancelled-packing";
                        var orderStatusClass = object.order_status == "wc-processing" ? "processing" : object.order_status == "wc-preparing" ? "preparing" :
                            object.order_status == "wc-shipped" ? "shipped" : object.order_status == "wc-completed" ? "completed" :
                            object.order_status == "wc-refunded" ? "refunded" : object.order_status == "wc-cancelled" ? "cancelled" :
                            object.order_status == "wc-on-hold" ? "on-hold" : "order-unshippable";

                        var formattedOrderDate = object.order_date == null ? "" : object.order_date;
                        var formattedAddress = object.address_1 == null ? "" : object.address_1.substr(0, 10) + "...";
                        var formattedCountryCode = object.country;

                        var formattedLabelNote = object.label_note == null ?
                            '<input class="order-note-input" type="text" onkeyup="bytesHandler(this);" placeholder="' + '발주 메모가 없습니다. 새로 입력하세요.' + '" value=""></input>' :
                            '<input class="order-note-input" type="text" onkeyup="bytesHandler(this);"  value="' + object.label_note + '"></input>';
                        var formattedLabelNoteLength = object.label_note == null ? 0 : getTextLength(object.label_note);

                        tr += '<tr pack-seq="' + object.pack_seq + '">' +

                            '<td class="valid-date">' + formattedValidDate + '</td>' +
                            '<td class="tracking-no"><a href="/shipment/loadShippingLabel?fileName=' + object.shipping_label_url + '" target="blank">' + object.tracking_no + '</a></td>' +
                            '<td class="order-no">\
                                    <a href="https://www.hanpoom.com/wp-admin/post.php?post=' + object.order_no + '&action=edit">' + object.order_no + '</a>' +
                            '</td>' +
                            '<td class="shipping-method">' + object.shipping_method + '</td>' +
                            '<td class="pic">' + object.pic + '</td>' +

                            '<td class="valid-time">' + object.valid_time + '</td>' +
                            '<td><span class="' + packStatusClass + '">' + object.process_status + '</span></td>' +
                            '<td class="order-date">' + formattedOrderDate + '</td>' +
                            '<td><span class="' + orderStatusClass + '">' + orderStatusClass + '</td>' +

                            '<td class="customer-name">' + object.customer_name + '</td>' +
                            '<td class="email"><a href="mailto:' + object.email + '">' + object.email + '</a></td>' +
                            '<td class="consignee">' + object.consignee + '</td>' +
                            '<td class="address_1">' + formattedAddress + '</td>' +

                            '<td class="city">' + object.city + '</td>' +
                            '<td class="state">' + object.state + '</td>' +
                            '<td class="postcode">' + object.postcode + '</td>' +
                            '<td class="country">' + formattedCountryCode + '</td>' +
                            '<td class="total">' + object.total + '</td>' +
                            '</tr>' +

                            '<tr class="label-note">' +
                            '<td colspan="1" class="label-note-title">발주 메모: </td>' +
                            '<td colspan="11">' +
                            formattedLabelNote +
                            '</td>' +
                            '<td class="order-note-bytes" colspan="2">' +
                            formattedLabelNoteLength + '/100 자' +
                            '</td>' +
                            '<td colspan="1">' +
                            '<input type="button" class="update-order-note-btn" value="수정">' +
                            '</td>' +
                            '<td colspan="4" class="order-note-msg-success">' +
                            '</td>' +
                            '</tr>';
                    });
                    $('tbody#historyList').html(tr);
                }
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                var tr = '<tr class="result-message">\
                            <td colspan="19">\
                                검색 결과가 없습니다.\
                            </td>\
                        </tr>';
                $('tbody#historyList').html(tr);
            } else {
                console.log(response.message);
            }
            $('.loading').css('display', 'none');
        }
    });
}

function getOrderListCount() {
    $.ajax({
        url: '/shipment/getShippingLabelHistoryCount?keyword=' + searchKeyword + '&date=' + orderDate +
            '&is_urgent=' + isUrgent + '&see_all=' + seeAll + '&is_today=' + isToday,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                const jsonObject = response.object;
                count = Number(jsonObject);
                $("#totalOrders").text(count);
                totalPage = Math.ceil(count / itemRange);
                createPagination(currentPage, totalPage);
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                $("#totalOrders").text(0);
                totalPage = Math.ceil(count / itemRange);
                createPagination(currentPage, totalPage);
            } else {
                console.log(response.message);
                // console.log("An error has occurred.");
            }
        },
        async: true,
        error: function() {
            console.log("error");
        }
    });
}


function updateLabelNote(orderNo, trackingNo, labelNote) {
    var dataSet = {
        "order_no": orderNo,
        "tracking_no": trackingNo,
        "label_note": labelNote
    };
    $.ajax({
        url: '/shipment/updateLabelNote',
        type: 'POST',
        data: JSON.stringify(dataSet),
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function(response) {
            if (response.code == '200') {
                $(".order-note-msg-success").html('<i class="fas fa-exclamation-circle"></i>운송장 메모 정상 갱신 완료.').removeClass("success").addClass("warning");
                $(".order-note-msg-success").animate({ opacity: 1 }, 500, function() {
                    $('.order-note-msg-success').css('visibility', 'visible');
                    $(".order-note-msg-success").animate({ opacity: 0 }, 3000, function() {
                        $('.order-note-msg-success').css('visibility', 'hidden');
                    });
                });
            } else if (response.code == '403') {
                window.location.href = '/signIn';
            } else if (response.code == '404') {
                $(".order-note-msg-fail").html('<i class="fas fa-exclamation-circle"></i>>운송장 메모 처리 실패.').removeClass("success").addClass("warning");
                $(".order-note-msg-fail").animate({ opacity: 1 }, 500, function() {
                    $('.order-note-msg-fail').css('visibility', 'visible');
                    $(".order-note-msg-fail").animate({ opacity: 0 }, 3000, function() {
                        $('.order-note-msg-fail').css('visibility', 'hidden');
                    });
                });
            } else {
                console.log(response.message);
                // console.log("An error has occurred.");
            }
        },
        async: true,
        error: function() {
            console.log("error");
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

    // getOrderList();
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


Date.prototype.format = function(f) {
    if (!this.valueOf()) return " ";
    var weekKorName = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
    var weekKorShortName = ["일", "월", "화", "수", "목", "금", "토"];
    var weekEngName = ["Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
    var weekEngShortName = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    var d = this;

    return f.replace(/(yyyy|yy|MM|dd|KS|KL|ES|EL|HH|hh|mm|ss|a\/p)/gi, function($1) {
        switch ($1) {
            case "yyyy":
                return d.getFullYear(); // 년 (4자리)
            case "yy":
                return (d.getFullYear() % 1000).zf(2); // 년 (2자리)
            case "MM":
                return (d.getMonth() + 1).zf(2); // 월 (2자리)
            case "dd":
                return d.getDate().zf(2); // 일 (2자리)
            case "KS":
                return weekKorShortName[d.getDay()]; // 요일 (짧은 한글)
            case "KL":
                return weekKorName[d.getDay()]; // 요일 (긴 한글)
            case "ES":
                return weekEngShortName[d.getDay()]; // 요일 (짧은 영어)
            case "EL":
                return weekEngName[d.getDay()]; // 요일 (긴 영어)
            case "HH":
                return d.getHours().zf(2); // 시간 (24시간 기준, 2자리)
            case "hh":
                return ((h = d.getHours() % 12) ? h : 12).zf(2); // 시간 (12시간 기준, 2자리)
            case "mm":
                return d.getMinutes().zf(2); // 분 (2자리)
            case "ss":
                return d.getSeconds().zf(2); // 초 (2자리)
            case "a/p":
                return d.getHours() < 12 ? "오전" : "오후"; // 오전/오후 구분

            default:
                return $1;
        }
    });
};
String.prototype.string = function(len) {
    var s = '',
        i = 0;
    while (i++ < len) { s += this; }
    return s;
};
String.prototype.zf = function(len) { return "0".string(len - this.length) + this; };
Number.prototype.zf = function(len) { return this.toString().zf(len); };