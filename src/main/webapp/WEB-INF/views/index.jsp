<%@ page import="org.example.searchbookmark.model.vo.KeywordSearch" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>검색을 할거에요</title>
</head>
<body>
    <h1>검색하자!</h1>
    <section style="display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 12px">
        <%
            for (KeywordSearch v : (List<KeywordSearch>) request.getAttribute("result")) {
        %>
        <div>
            <h3><%= v.title() %></h3>
            <ul>
                <li><%= v.link() %></li>
                <li><%= v.description() %></li>
                <li><%= v.date() %></li>
<%--                    <li><%= v.createdAt() %></li>--%>
            </ul>
        </div>
            <%
            }
        %>
    </section>
</body>
</html>