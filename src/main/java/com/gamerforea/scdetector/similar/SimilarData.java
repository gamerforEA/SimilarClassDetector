package com.gamerforea.scdetector.similar;

public class SimilarData implements Comparable<SimilarData>
{
	public final ClassContainer classContainer;

	public SimilarData(ClassContainer classContainer)
	{
		this.classContainer = classContainer;
	}

	public GsonSimilarData toGson()
	{
		return new GsonSimilarData(this);
	}

	@Override
	public String toString()
	{
		return this.classContainer.name;
	}

	@Override
	public int compareTo(SimilarData o)
	{
		return this.toString().compareTo(o.toString());
	}

	public static class GsonSimilarData
	{
		public final String name;

		public GsonSimilarData(SimilarData data)
		{
			this.name = data.classContainer.name;
		}
	}
}