<%@ page import="org.example.searchbookmark.model.vo.KeywordSearch" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>검색 결과</title>
    <style>
        body {
            font-family: sans-serif;
            margin: 20px;
        }

        h1 {
            color: #333;
            margin-bottom: 20px;
        }

        form {
            margin-bottom: 20px;
        }

        label {
            display: inline-block;
            margin-right: 10px;
        }

        input[type="text"] {
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        input[type="submit"] {
            padding: 8px 16px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        input[type="submit"]:hover {
            background-color: #0056b3;
        }

        section {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            gap: 12px;
        }

        div {
            border: 1px solid #ddd;
            padding: 15px;
            border-radius: 8px;
            width: 80%;
            max-width: 600px;
        }

        h3 {
            margin-bottom: 10px;
        }

        ul {
            list-style-type: none;
            padding: 0;
        }

        li {
            margin-bottom: 5px;
        }

        button {
            padding: 8px 16px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        button:hover {
            background-color: #218838;
        }
    </style>
</head>
<body>
<h1>검색 결과</h1>
<form>
    <label>키워드: <input name="keyword"></label>
    <input type="submit" value="검색">
</form>
<% if (request.getAttribute("result") != null) { %>
<section>
    <%
        for (KeywordSearch v : (List<KeywordSearch>) request.getAttribute("result")) {
    %>
    <div>
        <h3><%= v.title() %></h3>
        <ul>
            <li><a href="<%= v.link() %>" target="_blank"><%= v.link() %></a></li>
            <li><%= v.description() %></li>
            <li>날짜: <%= v.date() %></li>
            <form action="bookmark" method="post">
                <input type="hidden" name="uuid" value="<%= v.uuid() %>">
                <button>북마크 추가</button>
            </form>
        </ul>
    </div>
    <%
        }
    %>
</section>
<% } %>
</body>
</html>