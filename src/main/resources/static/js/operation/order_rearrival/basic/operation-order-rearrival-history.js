//global variables
var item_range, current_page, count, totalPage, sort, order;

$(document).ready(function () {
    item_range = $("#item_range").val();
    current_page = $("#listPage").val();
    sort = $("#sortBy").val();
    order = $("#orderBy").val();

    getOrderReArrivalListCount();

    $('#item_range').change(function () {
        current_page = 1;
        $("#listPage").val(current_page);
        item_range = $(this).val();
        getOrderReArrivalListCount();
    });

    $("#orderDate, #orderNo, #trackingNo, #createUser, #processDate").on("click", function () {
        order = $(this).attr("data-order");
        sort = $(this).attr("data-sort");

        if (order == 1) {
            order = $(this).attr("data-order", 0);
            getOrderReArrivalListCount();
            $("#orderBy").val(order);
            $("#sortBy").val(sort);
        } else {
            order = $(this).attr("data-order", 1);
            getOrderReArrivalListCount();
            $("#orderBy").val(order);
            $("#sortBy").val(sort);
        }

    });
});

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

    getOrderReArrivalList(current, item_range, sort, order);
    console.log(sort);

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

function getOrderReArrivalList(page, item_range, sort, order) {
    $.ajax({
        url: '/operation/getOrderReArrivalHistoryVO?page=' + page + '&item_range=' + item_range + '&sort=' + sort + '&order=' + order,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            $.each(jsonObject, function (i, object) {
                var tracking_no = object.tracking_no == null ? "미발급" : object.tracking_no;
                tr += '<tr><td>' +
                    object.shipment_date + '</td><td>' +
                    object.order_no + '</td><td>' +
                    tracking_no + '</td><td>' +
                    object.create_user + '</td><td>' +
                    object.create_date + '</td><td>' +
                    object.disposed_count + '</td><td>' +
                    object.restocked_count + '</td><td>' + 
                    object.percentage + '</td></tr>';
            });
            $('tbody#productList').html(tr);
        },
        async: true,
        error: function () {
            console.log("error");
        }
    });
}

function getOrderReArrivalListCount() {
    $.ajax({
        url: '/operation/getOrderReArrivalHistoryTotalCount',
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            $("#totalProducts").text(response.object);
            count = response.object;
            totalPage = Math.ceil(count / item_range);
            createPagination(current_page, totalPage);
        },
        async: true,
        error: function () {
            console.log("error");
        }
    });
}