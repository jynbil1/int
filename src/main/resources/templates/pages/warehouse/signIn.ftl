<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>로그인</title>

  <link href="/layout/styles/login.css??new Date().getTime()" rel="stylesheet" type="text/css">

  <script type="text/javascript" src="/js/sign-in/basic/login-jquery.js?new Date().getTime()"></script>
  <script type="text/javascript" src="/js/sign-in/basic/sign-up-post-2.js?new Date().getTime()"></script>

</head>
<body style="padding-top:0px; background-image: url(../images/wms/bg/sign-in/logistics-bg-img-1.jpg);">
<div class="loading">
    <div class="content">
    <img src="https://media.hanpoom.com/wp-content/uploads/2021/07/loading.gif" />
    <p>처리 중입니다... 기다려주세요.</p>
    </div>
</div>
  <ul class="faico clear">
  </ul>

  <div class="login-wrap">
	<div class="login-html">
	<div class="login-container">
		<div class="logo">
			<img height="118" src="https://media.hanpoom.com/wp-content/uploads/2021/07/Artboard-18.png" class="header-logo" alt="한품">
		</div>
		<input id="tab-1" type="radio" name="tab" class="sign-in" checked>
		<label for="tab-1" class="tab">로그인</label>
		<input id="tab-2" type="radio" name="tab" class="sign-up">
		<label for="tab-2" class="tab">회원가입</label>
		<div class="login-form">
			<form id="customer-form-1" method="post" action="/signIn" >
				<div class="sign-in-htm">
					<div class="group">
						<label for="user" class="label">아이디</label>
						<input name="user_id" id="user-id" type="text" class="input" placeholder="아이디를 입력해주세요" required>
					</div>
					<div class="group">
						<label for="pass" class="label">비밀번호</label>
						<input name="user_pw" type="password" class="input" data-type="password" placeholder="비밀번호 입력해주세요" required>
					</div>
					<div class="group">
						<input type="submit" class="button" value="로그인" id="sumbit-1">
						<div class="forgot-pass">
							<span id="requestNewPassword">비밀번호를 잊으셨나요?</span>
						</div>
						<div class="group">
							<span class="warning-msg"></span>
							<span class="success-msg"></span>
						</div>
					</div>
					<#if error??>
						<span class="error">${error}</span>
					</#if>
					
				</div>
			</form>
			<form id="customer-form-2">
				<div class="sign-up-htm">
					<div class="group">
						<label for="user" class="label">아이디</label>
						<input required id="reg-user-id"  type="text" class="input" placeholder="아이디">
						<span class="status-1"></span>
						<span class="status-11"></span>
					</div>
					<div class="group">
						<label for="pass" class="label">비밀번호</label>
						<input required id="reg-user-password" type="password" class="input" data-type="password" placeholder="비밀번호">
						<span class="status-2"></span>
						<span class="status-22"></span>
					</div>
					<div class="group">
						<label for="pass" class="label">이름</label>
						<input required id="reg-username" type="text" class="input" placeholder="이름">
					</div>
					<div class="group">
						<button type="submit" class="button" value="가입하기" id="submit-2">가입하기</button>
					</div>
				</div>
			</form>
		</div>

		
	</div><!-- end login-container -->

	
	
	</div>
	</div>
	<div class="login-footer">
						<p>Cross-Border Ecommerce</p>
						<h4>Logistics Management System</h4></p>
						<p><span>Designed and built by Hanpoom</span></p>
					</div>
</body>
</html>
