$(document).ready(function() {
  var currentPage = "";
  var currentItemRange = "";
  var totalCount = $("#countofproducts").val();
  var totalPages = "";


  if (getParameterValue()["item_range"] != "" && getParameterValue()["item_range"] != null) {
    currentItemRange = getParameterValue()["item_range"];
  } else {
    currentItemRange = 10;
  }

  totalPages = Math.round(totalCount / currentItemRange);

  if (getParameterValue()["page"] != "" && getParameterValue()["page"] != null) {
    currentPage = getParameterValue()["page"];
    if (totalPages == currentPage) {
      $("#btn_next").css("display", "none");
    } else if (currentPage == 1) {
      $("#btn_prev").css("display", "none");
    } else {
      $("#btn_prev").css("display", "inline");
      $("#btn_prev").css("display", "inline");
    }
  } else {
    $("#btn_prev").css("display", "none");
    currentPage = 1;
  }
  
  if (currentItemRange != "" && currentItemRange != null) {
    $("select#itemRange option").each(function () {
      if ($(this).val() == currentItemRange) {
        $(this).attr("selected", "selected");
      }
    });
  }

  if (getParameterValue()["keyword"] != "" && getParameterValue()["keyword"] != null) {
    $("#s-keyword").val(decodeURIComponent(getParameterValue()["keyword"]));
    $(".clear").css("display","block");
  }

  $("#btn_prev").click(function () {
    if (currentPage != "" && currentPage != null) {
      var page = parseInt(currentPage);
      var pageRedirect = parseInt(page - 1);
      navigateNextPrev(pageRedirect);
    }
  });

  $("#btn_next").click(function () {
    if (currentPage != "" && currentPage != null) {
      var page = parseInt(currentPage);
      var pageRedirect = parseInt(page + 1);
      navigateNextPrev(pageRedirect);
    }
  });

  $('select#itemRange').change(function () {
    var itemRange = $(this).val();
    setItemRange(itemRange);
  });

  $(".search > button.search").click(function (event) {
    event.preventDefault();
    var keyword = $("#s-keyword").val().trim();

    if (keyword != null && keyword != "") {
      searchKeyword(keyword);
    } else {
      alert("Please input search keyword.");
    }
  });

  $("#s-keyword").on('keyup', function (event) {
    if (event.keyCode === 13) {
      var keyword = $("#s-keyword").val().trim();

      if (keyword != null && keyword != "") {
        searchKeyword(keyword);
      } else {
        alert("Please input search keyword.");
      }
    }
  });

  $(".search > button.clear").click(function (event) {
    event.preventDefault();
    var url = new URL(window.location.href.split("?")[0]);
    window.location = url;
  });
});

function getParameterValue() {
  var params = [], hash;
  var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
  for (var i = 0; i < hashes.length; i++) {
    hash = hashes[i].split('=');
    params.push(hash[0]);
    params[hash[0]] = hash[1];
  }
  return params;
}

function setItemRange(item_range) {
  var url = new URL(window.location.href.split("?")[0]);
  var search_params = url.searchParams;
  search_params.set('item_range', item_range);
  url.search = search_params.toString();
  var new_url = url.toString();
  window.location = new_url;
}

function navigateNextPrev(page) {
  var url = new URL(window.location.href);
  var search_params = url.searchParams;
  search_params.set('page', page);
  url.search = search_params.toString();
  var new_url = url.toString();
  window.location = new_url;
}

function searchKeyword(keyword) {
  var url = new URL(window.location.href.split("?")[0]);
  var search_params = url.searchParams;
  search_params.set('keyword', keyword);
  url.search = search_params.toString();
  var new_url = url.toString();
  window.location = new_url;
}