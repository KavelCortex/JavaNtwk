于老师：

您好，我是本学期互联网编程课上的学生王嘉维 (2014180065), 在这次实验中遇到了一个问题，百度无果，希望向老师请教。

本次实验要求进行文件类型的判定。按照教程，我尝试使用了 `URLConnection.guessContentTypeFromStream(InputStream)`方法进行判定。按照预想，该静态方法将从输入流中获取到文件的头信息并以此来判定文件类型：

```java
    String query = "https://www.baidu.com/img/bd_logo1.png";
    URL urlQuery = new URL(query);
    URLConnection connection = urlQuery.openConnection();
    connection.connect();
    BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
    String fileType = URLConnection.guessContentTypeFromStream(bis);
```

可是实际上，无论`query`中输入任何内容，最后得到的`fileType`都为`null`。而在调试窗口中却发现无论是`URLConnection`还是`BufferedInputStream`均正常解析该地址，唯独`fileType`返回的是`null`。请问这是什么原因造成的？