run("Duplicate...", "title=cor");
run("Split Channels");
selectWindow("cor (red)");
run("Mean...", "radius=10");
setOption("BlackBackground", true);
run("Make Binary");
run("Variance...", "radius=1");