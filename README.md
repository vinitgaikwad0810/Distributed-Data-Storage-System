# ProjectFluffy
Gash 275 Project1

Developed Java-based storage servers using Netty Framework and designed protocol buffers message format for use in communication,

Implemented RAFT consensus algorithm for fault tolerance, Implemented work queues using RabbitMQ for task-distribution,

Coded data access layer for relational database Postgres and Cassandra.		



Project Requirements for the course:


Project Fluffy

Project Fluffy

Storing data in the cloud or on a network of servers share a common challenge -
how to securely share, store, and survive failures and corruption of data. There are
systems like Drop Box, Sync, Google Drive, and server installations like HDFS and
databases like Mongo that use multiple servers and redundant storage approaches.
As consumers we are left with choosing one. What if we could use any number of
services?
Your team has been awarded the project to design and implement a new approach
to storing and finding data in a larger, decentralized, heterogeneous platform.
This undertaking may appear to be straightforward, and some of the concepts and
technologies are certainly that. The challenge is in combining and building loosely
coupled, dynamic networks where all nodes and technology stacks are not known.
There are no set software stacks and tools –bounds are fuzzy – fluffy so to speak.
Your team has been tasked to build the communication and balancing overlay
system to collect, store and stream data with emphasis on:

1. Cloud – server – personal device transparency

2. Balance work across stacks

3. Survive failures

To this goal, you are given the responsibility to design and build the FileEdge
architecture. This should include a scalability strategy to account for a massive
registration processes, distributed image storage, and search capabilities that
include real-time monitoring of work and data as they enter the network of servers.
A study conducted last quarter has identified that the best approach would be to
design a system that can be hosted on a series of compute platforms using multiple
international deployments. The study also concluded the candidate technologies for
the system should be:

Languages: Java, and Python (client API), C++ (client API)

Core Packages: Google Protobuf, Netty

Storage: In-memory database

Challenges: Leader election, replication, and work/data balance

These candidate languages/technologies (though not inclusive) would be best
utilized in the following configuration: The storage servers should be Java-based
and feeds (adding files) have dual Java and Python APIs. Further recommendations
where the use of a GIS database and metadata support with geographic
registration/searching.

CmpE 275 Project 1 15 Feb 2016

Users of the system will likely interact through any number of possible platforms
(application, a web, or mobile platforms) - you are building a toolkit/API to not only
for clients but also for server-side processes.
Note at this time the team is not directed to implement a web or mobile app. The
team's target is strictly the server-side infrastructure. However, the team will need
to evaluate the suitability of the API for a spectrum of client uses.

Communication:
Since you are building a package for hosting data, your teams must ensure that
multiple servers can interoperate to form dynamic 'overlay' fabric. Furthermore, it
is unknown how wide the network will form so, having each server know all nodes
is not possible.

To test the design and strengths of all implementations, each team need to interact
with deployments of other teams. Each team should support two storage solutions
of which 50% cannot overlap another team’s choice.

Source code:

The baseline code for the project is the code you have been using in the class labs.
Periodically, new releases of the code will be made available to provide you
examples of new concepts.
