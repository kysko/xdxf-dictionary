package com.xdxf.dictionary.sqlite;

public class Book {
    private Long id;
    private String fromLang;
    private String toLang;
    private String bookname;
    private String description;
    private boolean isDirty;
    public Book() {
    }

    
    public Book(Long id, String fromLang, String toLang, String bookname,String description,String isDirty) {
        this.id = id;
        this.fromLang = fromLang;
        this.toLang = toLang;
        this.bookname = bookname;
        this.description = description;
        this.isDirty = isDirty.equalsIgnoreCase("Y");
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFromLang() {
        return fromLang;
    }

    public void setFromLang(String fromLang) {
        this.fromLang = fromLang;
    }

    public String getToLang() {
        return toLang;
    }

    public void setToLang(String toLang) {
        this.toLang = toLang;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

   
    public boolean isDirty() {
		return isDirty;
	}


	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
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
        if (!(object instanceof Book)) {
            return false;
        }
        Book other = (Book) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }


	@Override
	public String toString() {
		return String.format("%s [%s - %s]",bookname,fromLang,toLang);//Book [id=" + id + ", fromLang=" + fromLang + ", toLang="
				//+ toLang + ", bookname=" + bookname + ", description="
				//+ description + "]";
	}

    
}
