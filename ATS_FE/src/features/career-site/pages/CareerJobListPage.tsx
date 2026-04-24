/**
 * Career site job listing page placeholder (public).
 * Will display published job postings for external candidates to browse and apply.
 */
const CareerJobListPage = () => {
  return (
    <div className="container mx-auto px-4 py-12">
      <div className="text-center">
        <h1 className="text-3xl font-bold tracking-tight sm:text-4xl">
          Join Our Team
        </h1>
        <p className="mt-4 text-lg text-muted-foreground">
          Explore open positions and find your next career opportunity.
        </p>
      </div>
      <div className="mt-12">
        <p className="text-center text-muted-foreground">
          Job listings will be displayed here.
        </p>
      </div>
    </div>
  );
};

export default CareerJobListPage;
