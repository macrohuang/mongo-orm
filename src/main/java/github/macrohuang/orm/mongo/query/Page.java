package github.macrohuang.orm.mongo.query;

import java.util.List;

public class Page<T> {
	private List<T> results;
	private int pageNum;
	private int pageSize;
	private int totalCount;

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public int getPageNum() {
		return pageNum;
	}

	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageCount() {
		if (totalCount > 0) {
			if (pageSize > 0) {
				return (totalCount - 1) / pageSize + 1;
			} else {
				return 1;
			}
		} else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Page [results=" + results + ", pageNum=" + pageNum + ", pageSize=" + pageSize + ", totalCount=" + totalCount + "]";
	}

}