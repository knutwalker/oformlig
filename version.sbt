git.baseVersion           in ThisBuild := "0.1.0"
git.gitTagToVersionNumber in ThisBuild := { tag: String => if(tag matches "[0-9]+\\..*") Some(tag) else None }
