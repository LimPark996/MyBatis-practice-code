<%@ page import="org.example.searchbookmark.model.vo.KeywordSearch" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <% KeywordSearch bookmark = (KeywordSearch) request.getAttribute("bookmark"); %>
    <title><%= bookmark.title() %></title>
    <meta property="og:title" content="<%= bookmark.title() %>" />
    <meta property="og:description" content="<%= bookmark.description() %>" />
    <meta property="og:url" content="<%= bookmark.link() %>" />
    <meta property="og:type" content="website" />
    <meta property="og:image" content="https://via.placeholder.com/300" /> <style>
    body {
        font-family: sans-serif;
        margin: 20px;
        background-color: #f4f4f4;
    }

    .bookmark-container {
        background-color: white;
        padding: 20px;
        border-radius: 8px;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        max-width: 600px;
        margin: 0 auto;
    }

    h1 {
        color: #333;
        margin-bottom: 10px;
    }

    a {
        color: #007bff;
        text-decoration: none;
    }

    a:hover {
        text-decoration: underline;
    }

    p {
        line-height: 1.6;
        color: #555;
    }

    .date {
        color: #888;
        font-size: 0.9em;
        margin-top: 10px;
    }

    form {
        margin-top: 20px;
        text-align: center;
    }

    button {
        padding: 10px 20px;
        background-color: #007bff;
        color: white;
        border: none;
        border-radius: 5px;
        cursor: pointer;
    }

    button:hover {
        background-color: #0056b3;
    }
</style>
</head>
<body>
<div class="bookmark-container">
    <h1><%= bookmark.title() %></h1>
    <a href="<%= bookmark.link() %>" target="_blank"><%= bookmark.link() %></a>
    <p><%= bookmark.description() %></p>
    <p class="date">날짜: <%= bookmark.date() %></p>
    <p class="date">생성일: <%= bookmark.createdAt() %></p>

    <form action="<%= request.getContextPath() %>">
        <button>돌아가기</button>
    </form>
</div>
</body>
</html>