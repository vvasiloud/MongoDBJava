/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package course;

import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlogPostDAO {
    DBCollection postsCollection;

    public BlogPostDAO(final DB blogDatabase) {
        postsCollection = blogDatabase.getCollection("posts");
    }

    // Return a single post corresponding to a permalink
    public DBObject findByPermalink(String permalink) {

        DBObject post = null;
        BasicDBObject query = new BasicDBObject("permalink",permalink);
        post = postsCollection.findOne(query);

        return post;
    }

    // Return a list of posts in descending order. Limit determines
    // how many posts are returned.
    public List<DBObject> findByDateDescending(int limit) {

        List<DBObject> posts = null;
        // Return a list of DBObjects, each one a post from the posts collection
        BasicDBObject query = new BasicDBObject();
        DBCursor cursor =  postsCollection.find().sort(new BasicDBObject("date", -1)).limit(limit);
        posts = cursor.toArray();

        return posts;

    }


    public String addPost(String title, String body, List tags, String username) {

        System.out.println("inserting blog entry " + title + " " + body);

        String permalink = title.replaceAll("\\s", "_"); // whitespace becomes _
        permalink = permalink.replaceAll("\\W", ""); // get rid of non alphanumeric
        permalink = permalink.toLowerCase();


        BasicDBObject post = new BasicDBObject();
        // Remember that a valid post has the following keys:
        // author, body, permalink, tags, comments, date
        //
        // A few hints:
        // - Don't forget to create an empty list of comments
        // - for the value of the date key, today's datetime is fine.
        // - tags are already in list form that implements suitable interface.
        // - we created the permalink for you above.

        // Build the post object and insert it

        Date date = new Date();
        List comments = new ArrayList<Object>();

        post.append("title",title)
                .append("author",username)
                .append("body",body)
                .append("permalink",permalink)
                .append("tags",tags)
                .append("comments",comments)
                .append("date",date);
        postsCollection.insert(post);

        return permalink;
    }




    // White space to protect the innocent








    // Append a comment to a blog post
    public void addPostComment(final String name, final String email, final String body,
                               final String permalink) {

        // Hints:
        // - email is optional and may come in NULL. Check for that.
        // - best solution uses an update command to the database and a suitable
        //   operator to append the comment on to any existing list of comments

        BasicDBObject comment = new BasicDBObject();
        comment.append("author", name).append("body", body);
        if(StringUtils.isNotBlank(email)) {
            comment.append("email", email);
        }

        BasicDBObject searchQuery = new BasicDBObject().append("permalink",permalink);
        postsCollection.update(searchQuery,new BasicDBObject("$push", new BasicDBObject("comments", comment)));

    }


}
