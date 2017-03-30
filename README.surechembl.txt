The git repository is cloned from the original svn repository, see the project homepage:
https://sourceforge.net/projects/jnati/

"master" branch is for original upstream from svn
"deb" branch is for debianization


If you want to setup svn-remote for fetching the latest changes from
the original SVN repository, add the following lines in .git/config:

[svn-remote "svn"]
        url = https://svn.code.sf.net/p/jnati/code
        fetch = jnati/trunk:refs/remotes/svn/trunk
        branches = jnati/branches/*:refs/remotes/svn/*
        tags = jnati/tags/*:refs/remotes/svn/tags/*
[svn]
        authorsfile = ./authors.txt

Note, that only jnati code is fetched from the original SVN repo, as it contains other
projects as well.

Fetch the data from SVN:
$ git svn fetch

After that "git branch -r" will show "svn/<smth>" remote branches.
