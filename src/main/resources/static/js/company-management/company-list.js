$(document).ready(function () {
  var item_range = $("#selectRange").val();
  var current_page = $("#listPage").val();
  var keyword = $(".search > input").val();
  var total_page = "";
  var total_count = "";
  var company_id = "";
  var result = "";

  if (current_page == "" || current_page == null || current_page == "1") {
    $("#btn_prev").css("display", "none");
  }

  if ($("#listPage").length) {
    getCompanyList(current_page, item_range, keyword);
    getListCounts(keyword, company_id);
  }
  
  $("#btn_next").click(function (event) {
    event.preventDefault();
    current_page = parseInt($("#listPage").val());
    result = parseInt(current_page + 1);
    total_count = $("#tablecount").text();
    item_range = $("#selectRange").val();
    total_page = Math.ceil(total_count / item_range);
    getCompanyList(result, item_range, keyword);

    $("#listPage").val(result);
    
    if ($('#btn_prev').css('display') == 'none') {
      $("#btn_prev").css("display", "inline");
    }
    if (total_page == $("#listPage").val()) {
      $(this).css("display", "none");
    }
  });

  $("#btn_prev").click(function (event) {
    event.preventDefault();
    current_page = parseInt($("#listPage").val());
    result = parseInt(current_page - 1);
    total_count = $("#tablecount").text();
    item_range = $("#selectRange").val();
    total_page = Math.ceil(total_count / item_range);
    getCompanyList(result, item_range, keyword);

    $("#listPage").val(result);

    if ($('#btn_next').css('display') == 'none') {
      $("#btn_next").css("display", "inline");
    }
    if ($("#listPage").val() == 1) {
      $(this).css("display", "none");
    }
  });

  $('#selectRange').change(function () {
    $("#listPage").val(1);
    item_range = $(this).val();
    current_page = 1;
    $("#btn_prev").css("display", "none");
    getCompanyList(current_page, item_range, keyword);
  });

  $("div.search button").click(function (event) {
    event.preventDefault();
    $("#listPage").val(1);
    current_page = 1;
    keyword = $(".search > input").val();
    item_range = $("#selectRange").val();
    getCompanyList(current_page, item_range, keyword);
    getListCounts(keyword, company_id);

    setTimeout(function () {
      total_count = $("#tablecount").text();
      total_page = Math.ceil(total_count / item_range);
        if (total_page <= 1) {
          $("#btn_prev").css("display", "none");
          $("#btn_next").css("display", "none");
        } else {
          $("#btn_prev").css("display", "");
          $("#btn_next").css("display", "");
        }
    }, 1500);
  });
});

function getCompanyList(page, item_range, keyword) {
  $.ajax({
    url: '/getCompany?page=' + page + '&item_range=' + item_range + '&keyword=' + keyword,
    type: 'POST',
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function (response) {
      const jsonObject = response.object;
      var tr = '';
      $.each(jsonObject, function (i, object) {
        var activated = object.comp_use_flag == "1" ? "Yes" : "No";
        tr += '<tr><td>'
          + object.comp_id + '</td><td>'
          + object.comp_name + '</td><td>'
          + object.biz_name + '</td><td>'
          + object.account_holder + '</td><td>'
          + object.hanpoom_pic + '</td><td>'
          + activated + '</td><td>'
          + '<a href="/edit-company?id=' + object.comp_id + '" target="_blank"> Edit </a></td></tr>';
      });
      $('tbody#companyList').html(tr);
    },
    error: function () {
      console.log("error");
    }
  });
}

function getListCounts(keyword, companyid) {
  $.ajax({
    url: '/getListCounts?keyword=' + keyword + '&company_id=' + companyid,
    type: 'POST',
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function (response) {
      const jsonObject = response.object;
      $.each(jsonObject, function (i, object) {
        $('span#tablecount').html(object.company_count);
      });
    },
    error: function () {
      console.log("error");
    }
  });
}