# 修改提交人信息
## 1、修改Git配置
> 只对以后的commit起效,对已提交的commit无效
```
# 查看Git配置
> git config --list
# 修改全局提交人姓名、邮箱
> git config --global user.name xxx
> git config --global user.email xxxxxx
# 修改当前项目的提交人姓名、邮箱
> git config user.name xxx
> git config user.email xxxxxx
```
## 2、修改已提交的commit的姓名和邮箱
- 1、git clone 全新的repo到本地
- 2、替换相关信息，复制以下信息，回车
    ```
    git filter-branch --env-filter '

    OLD_NAME="Your Old Name"
    OLD_EMAIL="your-old-email@example.com"
    CORRECT_NAME="Your Correct Name"
    CORRECT_EMAIL="your-correct-email@example.com"

    # 可以通过或关系重写多个用户名
    if [ "$GIT_COMMITTER_NAME" = "$OLD_NAME" ]
    then
        export GIT_COMMITTER_NAME="$CORRECT_NAME"
        export GIT_COMMITTER_EMAIL="$CORRECT_EMAIL"
        export GIT_AUTHOR_NAME="$CORRECT_NAME"
        export GIT_AUTHOR_EMAIL="$CORRECT_EMAIL"
    fi
    if [ "$GIT_COMMITTER_EMAIL" = "$OLD_EMAIL" ]
    then
        export GIT_COMMITTER_NAME="$CORRECT_NAME"
        export GIT_COMMITTER_EMAIL="$CORRECT_EMAIL"
        export GIT_AUTHOR_NAME="$CORRECT_NAME"
        export GIT_AUTHOR_EMAIL="$CORRECT_EMAIL"
    fi
    if [ "$GIT_AUTHOR_NAME" = "$OLD_NAME" ]
    then
        export GIT_COMMITTER_NAME="$CORRECT_NAME"
        export GIT_COMMITTER_EMAIL="$CORRECT_EMAIL"
        export GIT_AUTHOR_NAME="$CORRECT_NAME"
        export GIT_AUTHOR_EMAIL="$CORRECT_EMAIL"
    fi
    if [ "$GIT_AUTHOR_EMAIL" = "$OLD_EMAIL" ]
    then
        export GIT_COMMITTER_NAME="$CORRECT_NAME"
        export GIT_COMMITTER_EMAIL="$CORRECT_EMAIL"
        export GIT_AUTHOR_NAME="$CORRECT_NAME"
        export GIT_AUTHOR_EMAIL="$CORRECT_EMAIL"
    fi
    ' --tag-name-filter cat -- --branches --tags
    ```
- 3、提交至远程仓库
    ```
    git push --force --tags origin 'refs/heads/*'
    ```