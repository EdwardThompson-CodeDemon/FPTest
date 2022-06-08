
# CS FP SKELETON KEY

This is a finngerprint sdk to handle fingerprint matching for forensics and data authentication and verification.



## API Reference

#### Get ISO template from any fingerprint image in base64 string

```java
  public String ImageToIso(Bitmap bmp)
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `bmp` | `Bitmap image` | **Required**. JPEG or png Image loaded as a bitmap.This functions return |


#### Get ISO template from any fingerprint WSQ in base64 string

```java
  public String WsqToIso(byte[] wsq)
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `wsq` | `WSQ compressed image` | **Required**. WSQ compressed image in bytes[] |

#### Match two fingerprints 

```java
  public public int matchISO(String fp1, String fp2) 
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `fp1` | `ISO Template` | **Required**. iso tempplate in base64 string|
| `fp2` | `ISO Template` | **Required**. iso tempplate in base64 string|


It returns the difference in fp images, consider difference less than 100 to be matching fingerprints




