var item_range, current_page, keyword, count, totalPage, sort, search_by, order;

$(document).ready(function () {
    item_range = $("#item_range").val();
    current_page = $("#listPage").val();
    keyword = $(".search > input").val();
    search_by = $("#selectRange").val();
    sort = $("#sortBy").val();
    order = $("#orderBy").val();

    getLowStockProductHistoryTotalCount(keyword, search_by);

    $("#searchBtn").on("click", function (event) {
        event.preventDefault();
        keyword = $(".search > input").val();
        current_page = 1;
        $("#listPage").val(current_page);
        search_by = $("#selectRange").val();
        createPagination(current_page, totalPage, sort, order);
        getLowStockProductHistoryTotalCount(keyword, search_by);
    });

    $('#item_range').change(function () {
        keyword = $(".search > input").val();
        current_page = 1;
        $("#listPage").val(current_page);
        item_range = $(this).val();
        search_by = $("#selectRange").val();
        createPagination(current_page, totalPage, sort, order);
        getLowStockProductHistoryTotalCount(keyword, search_by);
    });

    $("#prodID, #prodName, #prodBarcode, #prodRate, #prodLoc").on("click", function () {
        var order = $(this).attr("data-order");
        var sort = $(this).attr("data-sort");

        if (order == 1) {
            $(this).attr("data-order", 0);
            createPagination(current_page, totalPage, sort, order);
            $("#orderBy").val(order);
            $("#sortBy").val(sort);
        } else {
            $(this).attr("data-order", 1);
            createPagination(current_page, totalPage, sort, order);
            $("#orderBy").val(order);
            $("#sortBy").val(sort);
        }
    });
});

function createPagination(current, total, sort, order) {
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

    getLowStockProductHistoryList(current, item_range, search_by, keyword, sort, order);

    var pageTable = "<ul class='page-list'>";
    pageTable += "<li onclick='createPagination(" + '"1","' + totalPage + '","' + sort + '","' + order + '"' + ");'>&lt;&lt;</li>";

    for (var pageCell of rangeWithDots) {
        if (pageCell != '...') {
            pageTable += '<li onclick="createPagination(' + "'" + pageCell + "','" + totalPage + "','" + sort + "','" + order + "'" + ');">' + pageCell + '</li>';
        } else {
            pageTable += '<li>' + pageCell + '</li>';
        }
    }

    pageTable += "</li><li onclick='createPagination(" + '"' + totalPage + '","' + totalPage + '","' + sort + '","' + order + '"' + ");'>&gt;&gt;</li>";
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

function getLowStockProductHistoryList(page, item_range, search_by, keyword, sort, order) {
    $.ajax({
        url: '/getLowStockProductHistoryList?page=' + page + '&item_range=' + item_range + '&search_by=' + search_by + '&keyword=' + keyword + '&sort=' + sort + '&order=' + order,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            $.each(jsonObject, function (i, object) {
                var btn = "";
                if (object.processed_flag == 1) {
                    btn = '<button disabled id="complete_notification_btn" class="complete-notification disabled" data-id="' + object.product_id + '"><i class="material-icons-outlined">circle</i></button><button disabled id="delete_notification_btn" class="delete-notification disabled" data-id="' + object.product_id +'"><i class="material-icons-outlined">close</i></button>';
                } else {
                    btn = '<button id="complete_notification_btn" class="complete-notification success" data-id="' + object.product_id +'"><i class="material-icons-outlined">circle</i></button><button id="delete_notification_btn" class="delete-notification primary" data-id="' + object.product_id +'"><i class="material-icons-outlined">close</i></button>';
                }

                var createTime = (new Date(object.create_time)).toISOString().split('T')[0];
                var updateTime = (new Date(object.update_time)).toISOString().split('T')[0];

                var inputRemarks = "";

                if (object.processed_flag == 1) {
                    inputRemarks = '<input disabled type="text" name="remarks" value="' + object.remarks + '">';
                } else {
                    inputRemarks = '<input type="text" name="remarks">';
                }

                tr += '<tr id="' + object.product_id + '">'
                    + '<td class="product-id" data-label="상품 ID">' + object.product_id + '</td>'
                    + '<td class="product-name" data-label="상품 명">' + object.product_name + '</td>'
                    + '<td class="barcode" data-label="바코드">' + (object.barcode == "" ? "null"  : object.barcode) + '</td>'
                    + '<td class="product-rate" data-label="운영동 상품 적재율 (%)">' + object.product_load_rate + '</td>'
                    + '<td class="loc" data-label="운영동 위치">' + object.loc + '</td>'
                    + '<td class="create_time" data-label="접수 일자">' + createTime + '</td>'
                    + '<td class="create_user" data-label="접수자">' + object.create_user + '</td>'
                    + '<td class="processed_flag" data-label="처리 여부">' + object.processed_flag + '</td>'
                    + '<td class="update_time" data-label="처리 일자">' + updateTime + '</td>'
                    + '<td class="update_user" data-label="처리자">' + object.update_user + '</td>'
                    + '<td class="remarks" data-label="비고">' + inputRemarks + '</td>'
                    + '<td class="remarks">' + btn + '</td>'
                    + '</tr>';
            });
            $('#lowStockHistoryList').html(tr);
            console.log(response.object);

            $('.complete-notification').on("click", function () {
                var low_stock_product_id = $(this).attr("data-id");
                var remarks = $(this).parent().parent().find('[name="remarks"]').val();
                var lowStockProductHistContent = {
                    id: low_stock_product_id,
                    remarks: remarks,
                    processed_flag: "1",
                    use_flag: "1"
                };
                console.log(lowStockProductHistContent)
                $.ajax({
                    url: '/low-stock-product-history/update',
                    type: 'POST',
                    data: JSON.stringify(lowStockProductHistContent),
                    dataType: 'json',
                    contentType: 'application/json',
                    encode: true,
                    success: function (response) {
                        if (response.code === '200') {
                            console.log('Form submitted with content: ', lowStockProductHistContent);
                        } else if (response.code === '400') {
                            alert(response.message)
                        } else {
                            alert('Invalid');
                        }
                    }
                });
            });

            $('.delete-notification').on("click", function () {
                var low_stock_product_id = $(this).attr("data-id");
                var remarks = $(this).parent().parent().find('[name="remarks"]').val();
                var lowStockProductHistContent = {
                    id: low_stock_product_id,
                    remarks: remarks,
                    processed_flag: "1",
                    use_flag: "0"
                };
                console.log(lowStockProductHistContent)
                $.ajax({
                    url: '/low-stock-product-history/delete',
                    type: 'POST',
                    data: JSON.stringify(lowStockProductHistContent),
                    dataType: 'json',
                    contentType: 'application/json',
                    encode: true,
                    success: function (response) {
                        if (response.code === '200') {
                            console.log('Form submitted with content: ', lowStockProductHistContent);
                        } else if (response.code === '400') {
                            alert(response.message)
                        } else {
                            alert('Invalid');
                        }
                    }
                });
            });
        }
    });
}

function getLowStockProductHistoryTotalCount(keyword, search_by) {
    $.ajax({
        url: '/getLowStockProductHistoryTotalCount?keyword=' + keyword + '&search_by=' + search_by,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            $.each(jsonObject, function (i, object) {
                count = object.total_count;
                $("#totalCounts").text(count);
                totalPage = Math.ceil(count / item_range);
                createPagination(current_page, totalPage, sort, order);
            });
        },
        async: true,
        error: function () {
            console.log("error");
        }
    });
}
