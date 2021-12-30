<#escape x as x?html>
...
<#macro page title>
  <html>
    <head>
      <title>${title}</title>
      <link rel="stylesheet" href="/css/general/home-navbar.css?version=1.0">
      <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.13.0/css/all.css">
    </head>
  
    <#nested>
    <div class="topnav">
      <div class="desktop-nav">
        <div class="logo">
          <a href="/">
            <img src="https://media.hanpoom.com/wp-content/uploads/2021/07/Artboard-18.png" class="header-logo" alt="한품">
          </a>
        </div>

        <!-- new -->

        <div class="dropdown">

          <button class="dropbtn">Menu<i class="arrow down"></i>
          </button>

          <div class="dropdown-content">
          <div class="max-width">

            <div class="col">
              <h2>보관 센터 [STOCKING CENTER]</h2>
                <p><i class="dd-icon arrival"></i>입하 [ARRIVAL]</p>
                  <a href="/arrivalProductPage">상품 입하</a>
                  <a href="/arrivalExpdateProductPage">MD 상품 입하</a>
                  <a href="/arrival-product-history">입하 현황</a>

                <p><i class="dd-icon enter"></i>입고 [ENTER]</p>
                  <a href="/inProductPage">상품 입고</a>
                  <a href="/in-product-history">상품 입고 현황</a>
            </div>

            <div class="col">
              <h2>&nbsp;</h2>
                <p><i class="dd-icon move"></i>이동 [MOVE]</p>
                  <a href="/moveProductPage">상품 이동</a>
                  <a href="/stocking-move-product">보관동 이동</a>
                  <a href="/operation-move-product">운영동 이동</a>
                  <a href="/move-product-history">이동 현황</a>
                  <a href="/dMoveProductPage">즉시 이동</a>

                <p><i class="dd-icon loss"></i>손실 [LOSS]</p>
                  <a href="/loss-product">손실</a>
                  <a href="/loss-product-history">손실 현황</a>
            </div>
            <div class="col">
              <h2><a href="/operation">운영 센터 [OPERATION CENTER]</a></h2>
                <p><i class="dd-icon packing-slip"></i>주문서 [PACKING SLIP]</p>
                  <a href="/operation/order-packing-slip/validation">주문서 발급</a>
                  <a href="/operation/order-packing-slip/validation-history">발급 현황</a>


                <p><i class="dd-icon inspection"></i>검수 [INSPECTION]</p>
                  <a href="/operation/order-inspection-v2">주문 검수</a>

                <p><i class="dd-icon packing"></i>포장 [PACKING]</p>
                  <a href="/operation/order-packing">주문 포장</a>

                <p><i class="dd-icon confirmation"></i>확정 [CONFIRMATION]</p>
                  <a href="/operation/order-confirmation">주문 확정</a>

                <p><i class="dd-icon completion"></i>마감 [COMPLETION]</p>
                  <a href="/operation/routine-completion">센터 운영 마감</a>
                  <a href="/operation/routine-completion-history">센터 운영 마감 내역</a>
                
            </div>

            <div class="col">
              <h2>&nbsp;</h2>
                <p><i class="dd-icon order-unshippable"></i>미출고 [UNSHIPPABLE]</p>
                  <a href="/operation/order-unshippable-scan">상품 미출고 처리</a>
                  <a href="/operation/order-unshippable-history">상품 미출고 조회/현황</a>

                <p><i class="dd-icon re-arrival"></i>재입고 [RE-ARRIVAL]</p>
                  <a href="/operation/order-rearrival">상품 재입고</a>
                  <a href="/operation/order-rearrival-history">상품 재입고 현황</a>

                <p><i class="dd-icon stocking-picking"></i>보관동 피킹 [STK. AREA PICK]</p>
                  <a href="/operation/pickable-products-list">보관동 피킹 리스트 발급</a>
                  <a href="/validated-products-list">파악 리스트 발급 현황</a>

                <p><i class="dd-icon stock-req"></i>운영 재고 요청 [STOCK REQ.]</p>
                  <a href="/low-stock-product">재고 요청</a>
                  <a href="/low-stock-product-history">요청 현황</a>

                <p><i class="dd-icon search-operation"></i>주문서 운영 현황 [OP. STATS.]</p>
                  <a href="/operation/generic-search-order">조회</a>
            </div>

            <div class="col">
              <h2>상품 센터 [PRODUCT CENTER]</h2>
                <p><i class="dd-icon order"></i>발주 [ORDER]</p>
                  <a href="/orderProductPage">사입 발주 (Email)</a>
                  <a href="/recordOrderProductPage">사입 발주 (Email)(기록용)</a>
                  <a href="/siteOrderProductPage">사이트 발주</a>
                  <a href="/lowestpriceOrderPage">최적가 발주</a>
                  <a href="/mis/order/history">발주 기록</a>
                  <a href="/operation/common/hold-order">주문 보류</a>
                  <a href="/operation/common/unhold-order">주문 보류 해제</a>

                <p><i class="dd-icon management"></i>관리 [MANAGEMENT]</p>
                  <a href="/company-list" onclick="savepagecount1()">발주 업체</a>
                  <a href="/manager-list" onclick="savepagecount2()">발주 업체 담당자</a>
                  <a href="/md-product-list">발주 상품</a>
            </div>

            <div class="col">
              <h2>분석 센터 [ANALYSIS CENTER]</h2>
                <p><i class="dd-icon product"></i>상품 [PRODUCT]</p>
                  <a href="/searchProductPage">상품 조회</a>
                  <a href="/mis/order/history-view">발주 기록 (조회용)</a>
                  
                <p><i class="dd-icon shipment"></i>배송물 [SHIPMENT]</p>
                  <a href="/shippedOrderPage">주문 발송 내역</a>
                  <a href="/quantity-by-status">상품에 대한 상태별 수량</a>
                  <a href="/outProductPage">출고 상품 현황</a>
                  <a href="/shipment/label-history">운송장 발급 현황</a>

              <h2>회계 센터 [FINANCE CENTER]</h2>
                <p><i class="dd-icon transaction"></i>결제 [TRANSACTION]</p>
                <a href="/finance/order-transaction">결제서 관리</a>
            </div>
          </div>
          </div>
        </div>

      <!-- end new -->
        
        <div id="logout-button">
          <#if user_id??>
            <a href="/user" id="username">
              <i class="icon-user">
              </i> ${user_id} [내 정보]
            </a>
            <a href='/signOut'>로그아웃</a>
          <#else >
            <a href="/signIn">Sign In</a>
          </#if>
        </div>
      </div>
      <div class="mobile-nav">
        <div class="logo">
          <a href="/">
            <img src="https://media.hanpoom.com/wp-content/uploads/2021/07/Artboard-18.png" class="header-logo" alt="한품">
          </a>
        </div>
        <div class="nav-menu">
          <input type="checkbox" id="showMobileMenu">
          <label for="showMobileMenu">
            <i class="fa fa-bars"></i>
          </label>
        </div>
      </div>
  
  <div class="drawer-overlay" data-drawer-close="" tabindex="-1"></div>
  <div class="drawer-wrapper">
    <div class="drawer-header">
        <div class="drawer-title">
          <div class="logo-mobile"><img src="https://media.hanpoom.com/wp-content/uploads/2021/07/Artboard-18.png" class="header-logo" alt="한품"></div>
          
        <a href="/user">
          <i class="icon-user">
          </i> ${user_id} [내 정보]
          </a><a href="/signOut">로그아웃</a></div>
    </div>
    <div class="drawer-content">
        <nav class="drawer-menu-list">
          <ul>
              <li>
                보관 센터 [STOCKING CENTER]<i class="fa fa-angle-down"></i>
                <ul>
                    <li>
                      <p><i class="dd-icon arrival"></i>입하 [ARRIVAL]</p>
                      <ul class="menu-item">
                          <li><a href="/arrivalProductPage">상품 입하</a></li>
                          <li><a href="/arrivalExpdateProductPage">MD 상품 입하</a></li>
                          <li><a href="/arrival-product-history">입하 현황</a></li>
                      </ul>
                    </li>
                </ul>
                <ul>
                    <li>
                      <p><i class="dd-icon arrival"></i>입고 [ENTER]</p>
                      <ul class="menu-item">
                          <li><a href="/arrivalProductPage">상품 입고</a></li>
                          <li><a href="/arrivalExpdateProductPage">상품 입고 현황</a></li>
                      </ul>
                    </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon move"></i>이동 [MOVE]</p>
                      <ul class="menu-item">
                        <li><a href="/moveProductPage">상품 이동</a></li>
                        <li><a href="/stocking-move-product">보관동 이동</a></li>
                        <li><a href="/operation-move-product">운영동 이동</a></li>
                        <li><a href="/move-product-history">이동 현황</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon loss"></i>손실 [LOSS]</p>
                      <ul class="menu-item">
                        <li><a href="/loss-product">손실</a></li>
                        <li><a href="/loss-product-history">손실 현황</a></li>
                      </ul>
                  </li>
                </ul>
              </li>
              <li>운영 센터 [OPERATION CENTER]<i class="fa fa-angle-down"></i>
                <ul>
                  <li>
                      <p><i class="dd-icon packing-slip"></i>주문서 [PACKING SLIP]</p>
                      <ul class="menu-item">
                        <li><a href="/operation/order-packing-slip/validation">주문서 발급</a></li>
                        <li><a href="/operation/order-packing-slip/validation-history">발급 현황</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon inspection"></i>검수 [INSPECTION]</p>
                      <ul class="menu-item">
                        <li><a href="/operation/order-inspection-v2">주문 검수</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon packing"></i>포장 [PACKING]</p>
                      <ul class="menu-item">
                        <li><a href="/operation/order-packing">주문 포장</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon confirmation"></i>확정 [CONFIRMATION]</p>
                      <ul class="menu-item">
                        <li><a href="/operation/order-confirmation">주문 확정</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon completion"></i>마감 [COMPLETION]</p>
                      <ul class="menu-item">
                        <li><a href="/operation/routine-completion">센터 운영 마감</a></li>
                        <li><a href="/operation/routine-completion-history">센터 운영 마감 내역</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon order-unshippable"></i>미출고 [UNSHIPPABLE]</p>
                      <ul class="menu-item">
                        <li><a href="/operation/order-unshippable-scan">상품 미출고 처리</a></li>
                        <li><a href="/operation/order-unshippable-history">상품 미출고 조회/현황</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon re-arrival"></i>재입고 [RE-ARRIVAL]</p>
                      <ul class="menu-item">
                        <li><a href="/operation/order-rearrival">상품 재입고</a></li>
                        <li><a href="/operation/order-rearrival-history">상품 재입고 현황</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon stocking-picking"></i>보관동 피킹 [STK. AREA PICK]</p>
                      <ul class="menu-item">
                        <li><a href="/operation/pickable-products-list">보관동 피킹 리스트 발급</a></li>
                        <li><a href="/validated-products-list">파악 리스트 발급 현황</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon stock-req"></i>운영 재고 요청 [STOCK REQ.]</p>
                      <ul class="menu-item">
                        <li><a href="/low-stock-product">재고 요청</a></li>
                        <li><a href="/low-stock-product-history">요청 현황</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon search-operation"></i>주문서 운영 현황 [OP. STATS.]</p>
                      <ul class="menu-item">
                        <li><a href="/operation/generic-search-order">조회</a></li>
                      </ul>
                  </li>
                </ul>
              </li>
              <li>상품 센터 [PRODUCT CENTER]<i class="fa fa-angle-down"></i>
                <ul>
                  <li>
                      <p><i class="dd-icon order"></i>발주 [ORDER]</p>
                      <ul class="menu-item">
                        <li><a href="/orderProductPage">사입 발주 (Email)</a></li>
                        <li><a href="/recordOrderProductPage">사입 발주 (Email)(기록용)</a></li>
                        <li><a href="/siteOrderProductPage">사이트 발주</a></li>
                        <li><a href="/lowestpriceOrderPage">최적가 발주</a></li>
                        <li><a href="/order/order-product-history">발주 기록</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon management"></i>관리 [MANAGEMENT]</p>
                      <ul class="menu-item">
                        <li><a href="/company-list" onclick="savepagecount1()">발주 업체</a></li>
                        <li><a href="/manager-list" onclick="savepagecount2()">발주 업체 담당자</a></li>
                        <li><a href="/md-product-list">발주 상품</a></li>
                      </ul>
                  </li>
                </ul>
              </li>
              <li>분석 센터 [ANALYSIS CENTER]<i class="fa fa-angle-down"></i>
                <ul>
                  <li>
                      <p><i class="dd-icon product"></i>상품 [PRODUCT]</p>
                      <ul class="menu-item">
                        <li><a href="/searchProductPage">상품 조회</a></li>
                        <li><a href="/order/order-product-history-view">발주 기록 (조회용)</a></li>
                      </ul>
                  </li>
                </ul>
                <ul>
                  <li>
                      <p><i class="dd-icon shipment"></i>배송물 [SHIPMENT]</p>
                      <ul class="menu-item">
                        <li><a href="/shippedOrderPage">주문 발송 내역</a></li>
                        <li><a href="/quantity-by-status">상품에 대한 상태별 수량</a></li>
                        <li><a href="/outProductPage">출고 상품 현황</a></li>
                        <li><a href="/shipment/label-history">운송장 발급 현황</a></li>
                      </ul>
                  </li>
                </ul>
              </li>
              <li>회계 센터 [FINANCE CENTER]<i class="fa fa-angle-down"></i>
                <ul>
                  <li>
                      <p><i class="dd-icon transaction"></i>결제 [TRANSACTION]</p>
                      <ul class="menu-item">
                        <li><a href="/finance/order-transaction">결제서 관리</a></li>
                      </ul>
                  </li>
                </ul>
              </li>
          </ul>
        </nav>
    </div>
  </div>
  </div>



  <script>
    function savepagecount1() {
        
        var cntpage = 1;
        localStorage.setItem("cntpage", cntpage);
    }
    
    function savepagecount2() {
        
        var cntpage = 1;
        localStorage.setItem("cntpage", cntpage);
    }
  </script>

  <script>
  var mainNav = document.querySelector('.topnav');
    window.onscroll = function() {
      windowScroll();
    };

    function windowScroll() {
        mainNav.classList.toggle("disable-dropdown", mainNav.scrollTop > 20 || document.documentElement.scrollTop > 20);
    }

    $(window).scroll(function() {    
        var scroll = $(window).scrollTop();

        if (scroll >= 100) {
            $(".drawer-wrapper").addClass("sticky");
        } else {
            $(".drawer-wrapper").removeClass("sticky");
        }
    });

    $(document).ready(function() {
      $("#showMobileMenu").change(function(){
        if ($(this).is(':checked')) {
          $(".drawer-overlay, .drawer-wrapper").addClass("active");
          $("body").addClass("menu-opened");
        } else {
          $(".drawer-overlay, .drawer-wrapper").removeClass("active");
          $("body").removeClass("menu-opened");
        }
      });

      $(".drawer-overlay").on("click", function(){
        if ($(this).hasClass("active")) {
          $(this).removeClass("active");
          $(".drawer-wrapper").removeClass("active");
          $('#showMobileMenu').prop('checked', false);
          $("body").removeClass("menu-opened");
        }
      });

      $(".drawer-menu-list > ul > li").each(function() {
        $(this).on("click", function() {
          $(this).siblings().removeClass("expanded");
          $(this).siblings().find('ul').each(function() {
            $(this).hide();
          });
          $(this).siblings().children("i").addClass("fa-angle-down").removeClass("fa-angle-up");
          $(this).toggleClass("expanded");
          if ($(this).hasClass("expanded")) {
            $(this).children("i").addClass("fa-angle-up").removeClass("fa-angle-down");
            $(this).find('ul').each(function() {
                $(this).show();
            });
          } else {
            $(this).children("i").addClass("fa-angle-down").removeClass("fa-angle-up");
            $(this).find('ul').each(function() {
                $(this).hide();
            });
          }
        });
      });
      
    });
  </script>


  </html>
</#macro>
...
</#escape>
