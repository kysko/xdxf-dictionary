package com.xdxf.dictionary.sqlite;

public class Word {
	private Long id;
    private String word;
    private String meaning;
    private Long bid;

    public Word() {
    }


    public Word(Long id, String word, String meaning,Long bid) {
        this.id = id;
        this.word = word;
        this.meaning = meaning;
        this.bid = bid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

   

    public Long getBid() {
		return bid;
	}


	public void setBid(Long bid) {
		this.bid = bid;
	}


	@Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Word)) {
            return false;
        }
        Word other = (Word) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.almuallim.dictionary.entity.WordList[ id=" + id + " ]";
    }
}
