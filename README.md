# comp261A2
The comp261 Assignment 2, 2021, from VUW

By reading the slide, you may know obtain the basic understanding about  a* algorthim and articulation algorthim.
A* is about find the shortest route from startNOde to targetNode.
Acticulation point is the point that if it's removed, then the group of nodes will be divided to 2 groups even 3 groups or more.

Within the supplyFile directory, there are code and data files, you can download these 2 files and unzip them, import as the project.
And then read the handout, and start to write the project, it can help you to understand and practise the a* algorthim and articulation algorthim.

For my codes, I try some challenge but didnot complete it. For artPoint, it has the bug where sometimes the number of articulation point is incorrect. 
It should be 240 for small graph and 10856 for large, but sometime my algorthim will find 10857 and 241, sometime it is correct, pretty confuse.
If you find out how to solve this, please do the PR. Also, if you solve the challenge part please do the PR as well. :)

By the way, for articulation point algorthim, during the debugging time, I try to add Node.childNodes() collection inside Fringe and Node class
but it cannot work and finded artPoints are ridiculous incorrect, only got half correct numbers, like 100 points for small. 
Later, I find out that must modify the node.childNodes() inside the Mapper class.(you can access it from early commit.)
Therefore there are lots of redundant code, don't be confuse.


Finally,Do NOT copy my code since it won't help you understand the a* algorthim and articulation algorthim.


