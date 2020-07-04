# Glasses

![img](https://cdn.discordapp.com/attachments/714206938719715429/728780405762687046/Screenshot_20200703-211235.jpg)

All assets used were created by **[Poly by Google](https://poly.google.com/user/4aEd8rQgKu2)**. These were published under a Public/Remixable (CC-BY) license.

# Overview and Functionality

This application loads glasses on startup from a server using HTTP/post requests, and automatically loads them into the application. By pressing a button, you can swap the current viewed glasses on your face, similar to filters on Snapchat or Instagram. The glasses will automatically rerender themselves and size appropriately to your face.

# Coding Process

Creating the project was fairly simple, following a sample. The HTTP request loading did not take a significant amount of time either. Instead, sizing and scaling the glasses objects to the user's face took a considerably larger amount of time.

I ended up taking the sample program and moved the ModelRenderable initialization to its own function, which would remove the need for me to manually write that code. From there, I moved the model loading to a URI, and eventually put it onto a fileserver as well.

---

### Guides/References

-   [https://creativetech.blog/home/try-on-glasses-arcore-augmented-faces](https://creativetech.blog/home/try-on-glasses-arcore-augmented-faces)
    -   This is a sample project that I based a lot of the code from. I ended up spending a good amount of time refactoring how this code handles model and asset production.

# Errors Encountered

### [Android] Cleartext HTTP traffic is not permitted.

When swapping over from file-sharing at [localhost](http://localhost) to an ssh client, I ended up swapping from an HTTP protocol to an HTTPS protocol. Android by default does not permit cleartext messages or requests to HTTPS servers.

To mitigate this, (using the Stack Overflow article listed in Guides/References):

I created a `network_security_config.xml` file in `res/xml`.

```xml
<?xml version="1.0" encoding="utf-8"?>
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">api.example.com(to be adjusted)</domain>
    </domain-config>
</network-security-config>
```

Then, in the `AndroidManifest.xml` file:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest ...>
    <uses-permission android:name="android.permission.INTERNET" />
    <application
        ...
        android:networkSecurityConfig="@xml/network_security_config"
        ...>
        ...
    </application>
</manifest>
```

# License TL;DR

This project is distributed under the MIT license. This is a paraphrasing of a
[short summary](https://tldrlegal.com/license/mit-license).

This license is a short, permissive software license. Basically, you can do
whatever you want with this software, as long as you include the original
copyright and license notice in any copy of this software/source.

## What you CAN do:

-   You may commercially use this project in any way, and profit off it or the
    code included in any way;
-   You may modify or make changes to this project in any way;
-   You may distribute this project, the compiled code, or its source in any
    way;
-   You may incorporate this work into something that has a more restrictive
    license in any way;
-   And you may use the work for private use.

## What you CANNOT do:

-   You may not hold me (the author) liable for anything that happens to this
    code as well as anything that this code accomplishes. The work is provided
    as-is.

## What you MUST do:

-   You must include the copyright notice in all copies or substantial uses of
    the work;
-   You must include the license notice in all copies or substantial uses of the
    work.

If you're feeling generous, give credit to me somewhere in your projects.
