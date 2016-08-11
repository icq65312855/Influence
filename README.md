# Influence
Expansion of influence into fan-pages of Facebook

This project is aimed at investigation of the expansion of influence within the fan pages of online communities and allows define some patterns in the spread of influence. 

Questions:
Which percent of people are opinion leaders within big discussions?
Each user has a certain popularity. I measure popularity based on the number of received Likes and Comments of posts. Let each Like adds 1 point and each Comment adds 2 points to user popularity total score. Likes or Comments from the same user give less and fewer points.

Let's make it more complicated. The total score will be influenced by a popularity of user who gives Like or Comment, i.e when some user receives Like from a popular user he gets not 1point but its score increased in proportion to the popularity of user who gives Like. It seems logical because when we receive recognition from popular people our popularity grows up faster.

Opinion leaders are users whose total score of popularity more than the score of popularity rest of the users. I'm going to calculate their quantity and define which percent they represent. 

How is influence spreading within social groups?
A set of users whose post some user Like or Comment is named his “egonet” (personal network). Then let's choose from the user’s egonet whose post this user Likes or Comments the most and he becomes his “direct leader” and the user becomes his follower. All followers of direct leader are first level “followers group”. Thus, the leader "influence" on his followers. This is the first level influence. The force of the influence on each user is determined by the number of Likes or Comments received from this user. Also, I add the dependence of force of influence from the leader's popularity.

Now, let’s observe influence expansion. All users from the defined egonet with the same direct leader I call “adherents”, and an amount of influence on them by his leader is “power of adherence”. Then I take adherents with the largest power of adherence and get their leader.  Then switch our direct leader to the leader we get based on “power of adherence” and define a power of influence of the new leader as average power of adherents' commitment multiplied on “coefficient of an influence spreading”. Thus, the leader is expanding his influence to the second level. Now the new adherents of the leader can involve other users. It will be the influence of the third level, etc.

So, let’s investigate how the leaders' influence will be spreading? Will all users become the followers of one leader or equilibrium comes in some moment? Or the algorithm will never finish its processing:) What will happen if we change the “force of influence" coefficient?
