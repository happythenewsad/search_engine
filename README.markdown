Summary:
==


This was a definitive project for me at university. It's a (mostly) functional search engine and includes parsing, indexing, and query processing libraries. I used the [TREC](http://http://trec.nist.gov/) dataset for testing.

The key takeaways from this project were:

1. Grokking regular expressions
2. Grokking performance testing (writing to 100,000 files takes awhile, maybe I should index the index)
3. Code clarity is essential (I had to eat my own dog food!)

Please feel free to poke around - I hope you can learn from my mistakes and perhaps get some ideas if you are writing something similar. Keep in mind that many architecture decisions were requirements of the assignment. For instance, the parser assumes a very specialized document structure.