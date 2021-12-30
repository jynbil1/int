var total_manager = "";
var total_product = "";
var manageritemrange = 10;
var productitemrange = 10;

$(document).ready(function() {
  var companyid = getParameterValues()["id"];
  var managerpage = $("#manager-page").val();
  var productpage = $("#product-page").val();

  managerList(managerpage, manageritemrange, companyid);
  productList(productpage, productitemrange, companyid);
  getListCounts("", companyid);

  if (managerpage == 1) {
    $("#btn_prev_manager").css("display", "none");
  }

  if (productpage == 1) {
    $("#btn_prev_product").css("display", "none");
  }
  
  $('#edit-company-submit').on('click', function(event) {
    validateInputs();
  });

  $("#btn_next_manager").click(function (event) {
    event.preventDefault();
    var current_page = parseInt($("#manager-page").val());
    var result = parseInt(current_page + 1);
    $("#manager-page").val(result);

    setTimeout(function () {
      managerList(result, manageritemrange, companyid);
    }, 500);

    if (result == total_manager) {
      $(this).css("display", "none");
    }

    if (result > 1 && result <= total_manager) {
      $("#btn_prev_manager").css("display", "inline");
    }
  });

  $("#btn_prev_manager").click(function (event) {
    event.preventDefault();
    var current_page = parseInt($("#manager-page").val());
    var result = parseInt(current_page - 1);
    $("#manager-page").val(result);

    setTimeout(function () {
      managerList(result, manageritemrange, companyid);
    }, 500);

    if (result < total_manager) {
      $("#btn_next_manager").css("display", "inline");
    }

    if (result <= 1) {
      $(this).css("display", "none");
    }
  });

  $("#btn_next_product").click(function (event) {
    event.preventDefault();
    var current_page = parseInt($("#product-page").val());
    var result = parseInt(current_page + 1);
    $("#product-page").val(result);

    setTimeout(function () {
      productList(result, productitemrange, companyid);
    }, 500);

    if (result == total_product) {
      $(this).css("display", "none");
    }
    
    if (result > 1 && result <= total_product) {
      $("#btn_prev_product").css("display", "inline");
    }
  });

  $("#btn_prev_product").click(function (event) {
    event.preventDefault();
    var current_page = parseInt($("#product-page").val());
    var result = parseInt(current_page - 1);
    $("#product-page").val(result);

    setTimeout(function () {
      productList(result, productitemrange, companyid);
    }, 500);

    if (result < total_product) {
      $("#btn_next_product").css("display", "inline");
    }

    if (result <= 1) {
      $(this).css("display", "none");
    }
  });
});

function managerList(managerpage, itemrange, companyid) {
  $.ajax({
    url: '/getManager?page=' + managerpage + '&item_range=' + itemrange + '&company_id=' + companyid,
    type: 'POST',
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function (response) {
      const jsonObject = response.object;
      var tr = '';
      $.each(jsonObject, function (i, object) {
        var activated = object.mgr_use_flag == "1" ? "Yes" : "No";
        tr += '<tr><td>'
          + object.mgr_id + '</td><td>'
          + object.mgr_name + '</td><td>'
          + object.mgr_rank + '</td><td>'
          + object.mgr_email + '</td><td>'
          + object.mgr_number + '</td><td>'
          + activated + '</td></tr>';
      });
      $('#managerList > tbody').html(tr);
    },
    error: function () {
      console.log("error");
    }
  });
}

function productList(productpage, productitemrange, companyid) {
  $.ajax({
    url: '/getProduct?page=' + productpage + '&item_range=' + productitemrange + '&company_id=' + companyid,
    type: 'POST',
    contentType: "application/json; charset=utf-8",
    dataType: "json",
    success: function (response) {
      const jsonObject = response.object;
      var tr = '';
      $.each(jsonObject, function (i, object) {
        var activated = object.product_use_flag == "1" ? "Yes" : "No";
        tr += '<tr><td>'
          + object.product_id + '</td><td>'
          + object.product_name + '</td><td>'
          + object.unit_price + '</td><td>'
          + object.normal_price + '</td><td>'
          + object.margin + '</td><td>'
          + object.margin_rate + '</td><td>'
          + object.barcode + '</td><td>'
          + activated + '</td></tr>';
      });
      $('#productList > tbody').html(tr);
    },
    error: function () {
      console.log("error");
    }
  });
}

function getParameterValues()
{
    var params = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for(var i = 0; i < hashes.length; i++)
    {
        hash = hashes[i].split('=');
        params.push(hash[0]);
        params[hash[0]] = hash[1];
    }
    return params;
}

function validateInputs() {
  var comp_name = $("#company-name").val();
  var biz_name = $("#business-name").val();
  var biz_no = $("#business-number").val();
  var bank_name = $("#bank-name").val();
  var account_holder = $("#business-owner").val();
  var account = $("#bank-account-num").val();

  if (comp_name == "") {
    alert("Company Name must be filled out");
    return false;
  } else if (biz_name == "") {
    alert("Business Name must be filled out");
    return false;
  } else if (biz_no == "") {
    alert("Phone number must be filled out");
    return false;
  } else if (bank_name == "") {
    alert("Bank must be filled out");
    return false;
  } else if (account_holder == "") {
    alert("Business Owner must be filled out");
    return false;
  } else if (account == "" || account == 0) {
    alert("Account Number is required");
    return false;
  } else {
    submitForm();
    return true;
  }
}

function submitForm() {
  var comp_id = $("#comp-id").val();
  var comp_name = $("#company-name").val();
  var biz_name = $("#business-name").val();
  var biz_no = $("#business-number").val();
  var comp_use_flag = $("#post-status").val();
  var bank_name = $("#bank-name").val();
  var account_holder = $("#business-owner").val();
  var account = $("#bank-account-num").val();
  var transaction_req_date_type = $("#depositType").val();
  var order_type = $("#type").val();
  var hanpoom_pic = $("#hanpoom-pic").val();

  var editcompanycontent = {
    comp_id: comp_id,
    comp_name: comp_name,
    biz_name: biz_name,
    biz_no: biz_no,
    comp_use_flag: comp_use_flag,
    bank_name: bank_name,
    account_holder: account_holder,
    account: account,
    transaction_req_date_type: transaction_req_date_type,
    order_type: order_type,
    hanpoom_pic: hanpoom_pic
  };

  $.ajax({
    type: 'POST',
    contentType: "application/json",
    url: '/updateCompany',
    data: JSON.stringify(editcompanycontent),
    dataType: "json",
    success: function (response) {
      if (response.code == "200") {
        alert("Company's Data Have Been Updated.");
      } else {
        alert("An error has occurred.");
      }
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
      var managerTotal;
      $.each(jsonObject, function (i, object) {
        $('span#managertablecount').html(object.manager_count);
        $('span#producttablecount').html(object.product_count);
        total_manager = Math.ceil($("span#managertablecount").text() / manageritemrange);
        total_product = Math.ceil($("span#producttablecount").text() / productitemrange);

        if (total_manager <= 1) {
          $("#btn_next_manager").css("display","none");
          $("#btn_prev_manager").css("display", "none");
        }

        if (total_product <= 1) {
          $("#btn_next_product").css("display", "none");
          $("#btn_prev_product").css("display", "none");
        }
      });
    },
    error: function () {
      console.log("error");
    }
  });
}