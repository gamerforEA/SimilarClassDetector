package com.gamerforea.scdetector.similar;

public class SimilarData
{
	public final ClassContainer classContainer;
	public float similarStrings = 0F;
	public float similarFields = 0F;
	public float similarMethods = 0F;

	public SimilarData(ClassContainer classContainer)
	{
		this.classContainer = classContainer;
	}

	public GsonSimilarData toGson()
	{
		return new GsonSimilarData(this);
	}

	public static class GsonSimilarData
	{
		public final String name;
		public final float similarStrings;
		public final float similarFields;
		public final float similarMethods;

		public GsonSimilarData(SimilarData data)
		{
			this.name = data.classContainer.name;
			this.similarStrings = data.similarStrings;
			this.similarFields = data.similarFields;
			this.similarMethods = data.similarMethods;
		}
	}
}