var pageItem = "";
var itemRange = "";
var keyword = "";
var totalPage = "";
var count = "";

$(document).ready(function () {
    itemRange = $("#item_range").val();
    pageItem = $("#listPage").val();
    sort = $("#sortBy").val();
    order = $("#orderBy").val();

    var href = window.location.href;
    if (href.indexOf("-history") > -1) {
        getRoutineCompletionHistoryListCount();
    }

    $("#submitBtn").click(function (event) {
        event.preventDefault();
        var passcode = $("#enterprise").val();
        validatePasscode(passcode);
    });

    $("#enterprise").keypress(function (e) {
        var key = e.which;
        if (key == 13) {
            $("#submitBtn").click();
        }
    });

    $('#item_range').change(function () {
        pageItem = 1;
        $("#listPage").val(pageItem);
        itemRange = $(this).val();
        totalPage = Math.ceil(count / itemRange);
        createPagination(pageItem, totalPage);
    });

    $("#deliveryOption span").on("click", function () {
        $(this).addClass("active").siblings().removeClass("active");
    });
});

function getRoutineCompletionHistoryList(page, item_range, sort, order) {
    $.ajax({
        url: '/operation/getRoutineCompletionHistoryList?page=' + page + '&item_range=' + item_range + '&sort=' + sort + '&order=' + order,
        type: 'POST',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = response.object;
            var tr = '';
            $.each(jsonObject, function (i, object) {
                tr += '<tr>'
                    + '<td class="shipping-date">' + object.shipping_date + '</td>'
                    + '<td class="create-user">' + object.create_user + '</td>'
                    + '<td class="processed-date">' + object.processed_date + '</td>'
                    + '<td class="total-qty">' + object.total_qty + '</td>'
                    + '</tr>';
            });
            $('tbody#historyList').html(tr);
        },
        error: function () {
            console.log("error");
        }
    });
}

function getRoutineCompletionHistoryListCount() {
    $.ajax({
        url: '/operation/getRoutineCompletionHistoryCount',
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        success: function (response) {
            const jsonObject = Number(response.object);
            count = jsonObject;
            $("#totalProducts").text(count);
            totalPage = Math.ceil(count / itemRange);
            createPagination(pageItem, totalPage);
        },
        async: true,
        error: function () {
            console.log("error");
        }
    });
}

function validatePasscode(passcode) {
    $.ajax({
        url: '/operation/validateRoutineCompletionCode?code=' + passcode,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        dataType: "json",
        async: true,
        success: function (response) {
            if (response.code == '200') {
                var shipping = $("#deliveryOption > span.active").data("val");
                initiateRoutineCompletion(shipping);
            } else {
                alert('Invalid Passcode!');
            }
        },
        error: function () {
            console.log("error");
        }
    });
}

function initiateRoutineCompletion(shipping) {
    $(".progress-container").css("display", "table");
    $.ajax({
        url: '/operation/initiateRoutineCompletion?shipping=' + shipping,
        type: 'GET',
        contentType: "application/json; charset=utf-8",
        success: function (response) {
            progressBar(response.toString());
        },
        error: function () {
            console.log("error");
            $(".progress-container").css("display", "none");
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

    getRoutineCompletionHistoryList(current, itemRange, sort, order);

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

function progressBar(task_id) {
    var checked_count = 3;
    var counter = 0;
    var interval = null;
    var status = "in_progress";
    var message = "";

    if (checked_count > 0) {
        $(".progress-count").text("");
        $(".progress-bar-inner").css("width", "0%");

        var addInterval = function () {
            if (status == "in_progress") {
                $.ajax({
                    url: '/getRoutineCompletionTaskProgress?task_id=' + task_id,
                    type: 'GET',
                    contentType: "application/json; charset=utf-8",
                    dataType: "json",
                    success: function (response) {
                        const object = response.object;
                        $(".progress-bar-inner").each(function () {
                            counter = object.progress_count;
                            status = object.status;
                            message = object.message;
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
                        console.log("error");
                    }
                });
            } else if (status == "with_error") {
                alert(message);
                $(".progress-container").css("display", "none");
                clearInterval(interval);
            } else {
                window.location.href = "/operation/routine-completion-history";
                $(".progress-container").css("display", "none");
                clearInterval(interval);
            }
        };
        interval = setInterval(addInterval, 1000);
    }
}
