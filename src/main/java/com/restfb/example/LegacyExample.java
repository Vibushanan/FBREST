/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 Mark Allen, Norbert Bartels.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.restfb.example;

import static java.lang.String.format;
import static java.lang.System.out;
import static java.util.Collections.singletonList;

import com.restfb.DefaultLegacyFacebookClient;
import com.restfb.Facebook;
import com.restfb.LegacyFacebookClient;
import com.restfb.Parameter;

import java.util.List;

/**
 * Examples of RestFB's Legacy REST API functionality.
 * 
 * @author <a href="http://restfb.com">Mark Allen</a>
 */
public class LegacyExample extends Example {
  /**
   * RestFB Legacy REST API client.
   */
  private LegacyFacebookClient facebookClient;

  /**
   * Entry point. You must provide a single argument on the command line: a valid OAuth access token.
   * 
   * @param args
   *          Command-line arguments.
   * @throws IllegalArgumentException
   *           If no command-line arguments are provided.
   */
  public static void main(String[] args) {
	  String token="EAAKpqdBz5EgBAFnS6sK67Yo4nZAkTbVtP7Q7ZBgKijyhKIOeJC2idcqEpOkoijOmTqK6HH9ZBh6gt00ED5DlgoZChdVXT3yZBtD6BG4bYmqLAlrrdYZAdkqjSVVmdeVKlZCuHl6rQbZAkwuZCdYgJ46qxJFzWBQzlUQNXWEJG4lmyGQZDZD";
    new LegacyExample(token).runEverything();
  }

  LegacyExample(String accessToken) {
    facebookClient = new DefaultLegacyFacebookClient(accessToken);
  }

  void runEverything() {
    object();
    list();
    publish();
  }

  void object() {
    Long uid = facebookClient.execute("users.getLoggedInUser", Long.class);
    System.out.println("My UID is " + uid);
  }

  void list() {
    out.println("* Call that returns a list *");

    List<LegacyUser> users = facebookClient.executeForList("Users.getInfo", LegacyUser.class,
      Parameter.with("uids", "220439, 7901103"), Parameter.with("fields", "last_name, first_name"));

    out.println("Users: " + users);
  }

  /**
   * Holds user information as retrieved in {@link #list()}.
   */
  public static class LegacyUser {
    @Facebook("first_name")
    String firstName;

    @Facebook("last_name")
    String lastName;

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
      return format("%s %s", firstName, lastName);
    }
  }

  /**
   * Holds user information as retrieved in {@link #list()}.
   */
  public static class LegacyFqlUser {
    // By default, assumes JSON attribute name is the same as the Java field
    // name
    @Facebook
    String name;

    // If your Java field name is different than the JSON attribute name, just
    // specify the JSON attribute name
    @Facebook("pic_big")
    String pictureUrl;

    @Facebook
    List<Affiliation> affiliations;

    @Override
    public String toString() {
      return format("Name: %s\nProfile Image URL: %s\nAffiliations: %s", name, pictureUrl, affiliations);
    }
  }

  public static class Affiliation {
    @Facebook
    String name;

    @Facebook
    String type;

    @Override
    public String toString() {
      return format("%s (%s)", name, type);
    }
  }

  void publish() {
    out.println("* Publishes to your feed *");

    ActionLink category = new ActionLink();
    category.href = "http://bit.ly/KYbaN";
    category.text = "humor";

    Properties properties = new Properties();
    properties.category = category;
    properties.ratings = "5 stars";

    Medium medium = new Medium();
    medium.href = "http://bit.ly/187gO1";
    medium.src = "http://bit.ly/GaTlC";
    medium.type = "image";

    Attachment attachment = new Attachment();
    attachment.name = "i'm bursting with joy";
    attachment.href = "http://bit.ly/187gO1";
    attachment.caption = "{*actor*} rated the lolcat 5 stars";
    attachment.description = "a funny looking cat";
    attachment.properties = properties;
    attachment.media = singletonList(medium);

    // Send the request to Facebook.
    // We specify the fact that we're expecting a String response and that we're
    // passing along an attachment (defined above).

    String postId = facebookClient.execute("stream.publish", String.class, Parameter.with("attachment", attachment));

    out.println("ID of the post you just published: " + postId);
  }

  // Below are classes that get converted to JSON and passed along to the
  // stream.publish call.

  public static class ActionLink {
    @Facebook
    String text;

    @Facebook
    String href;
  }

  public static class Medium {
    @Facebook
    String type;

    @Facebook
    String src;

    @Facebook
    String href;
  }

  public static class Properties {
    @Facebook
    ActionLink category;

    @Facebook
    String ratings;
  }

  public static class Attachment {
    @Facebook
    String name;

    @Facebook
    String href;

    @Facebook
    String caption;

    @Facebook
    String description;

    @Facebook
    Properties properties;

    @Facebook
    List<Medium> media;
  }
}