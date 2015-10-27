<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Test Progess Popup</title>
<script type="text/javascript">
$(function(){
    $("#example-basic").steps({
        headerTag: "h3",
        bodyTag: "section",
        transitionEffect: "slideLeft",
        autoFocus: true
    });
})
</script>
</head>
<body>
    <div id="example-basic">
        <h3>step1</h3>
        <section>
            <p>step1 Contents</p>
        </section>
        <h3>step2</h3>
        <section>
            <p>step2 Contents</p>
        </section>
        <h3>step3</h3>
        <section>
            <p>step3 Contents</p>
        </section>
    </div>

</body>
</html>