<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Health Check</title>
 <style type="text/css"> 
    form {display: inline-block; } 
    p {display:inline-block}
</style> 
</head>
<body>
    <h3>register form</h3>
    <p>${result}</p><br>
    <form action=${registerpath} method="post" accept-charset="utf-8" modelAttribute="user"> 
        <p>${password}</p>
        <p>password</p><br>
        <input type="password" name="password"><br>
        <p>${confirmpassword}</p>
        <p>confirm password</p><br>
        <input type="password" name="confirmpassword"><br>
        <p>${firstname}</p>
        <p>first name</p><br>
        <input type="text" name="firstname"><br>
        <p>${lastname}</p>
        <p>last name</p><br>
        <input type="text" name="lastname"><br>
        <p>${phone}</p>
        <p>phone</p><br>
        <input type="text" name="phone"><br>
        <p>${email}</p>
        <p>email</p><br>
        <input type="email" name="email"><br>
        <input type="submit" value="submit">
        </form><br>
</body>
</html>