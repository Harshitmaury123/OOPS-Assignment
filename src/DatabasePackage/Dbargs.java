package DatabasePackage;

public class Dbargs {
    private static final String url="jdbc:mysql://localhost:3306/universitymanagement";
    private static final String username="root";
    private static final String pass="5466";

  public static String getUrl()
  {
      return url;
  }public  static String getUsername()
  {
      return username;
  }
  public  static String getpassword()
  {
      return pass;
  }

}

